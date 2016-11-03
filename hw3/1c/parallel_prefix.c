#include <errno.h>
#include <limits.h>
#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <time.h>

#define THREAD_NUM 128
#define MIN(a,b) (((a)<(b))?(a):(b))

typedef struct {
	double *a;
	double *orig;
	int count;
} pprefix_args_t;

void usage(int exit_status)
{
	fprintf(stderr, "Usage: parallel_prefix\n");
	fprintf(stderr, "Input is taken from stdin, one number per line\n");
	exit(exit_status);
}

void do_parallel_prefix(double *a, int count, const double *orig)
{
	double sum = 0;

	if (a[count] != orig[count] || count == 0)
		/* If a[count] was already modified, we're done */
		return;

	/* We iterate backwards until we find
	 * a value i, for which orig[i] != a[i]
	 * at that point, we can simply add
	 * a[i] to sum
	 */
	for (int i = count - 1; i >= 0; i--) {
		if (a[i] == orig[i]) {
			sum += orig[i];
		} else {
			/* Someone already modified a to contain
			 * the ith result, so add it and return
			 */
			sum += a[i];
			break;
		}
	}

	a[count] += sum;
}

void *do_parallel_prefix_cb(void *s)
{
	/* Boilerplate to actually call parallel_prefix, while matching
	 * the signature for pthread_create */
	pprefix_args_t *x = (pprefix_args_t*)s;
	do_parallel_prefix(x->a, x->count, x->orig);

	return NULL;
}


void parallelPrefixSum(double *a, int count)
{
	/* This algorithm creates a read-only copy of the array a (orig).
	 *
	 * It then spawns THREAD_NUM threads that work cooperatively
	 * to solve the problem.
	 *
	 * For instance for input, and assuming THREAD_NUM = 4
	 * a        = [1, 4, 9, 16]
	 * orig     = [1, 4, 9, 16]
	 *
	 * The ith thread is responsible for calculating the ith value.
	 * The original array (orig) is used as a means to verify if the
	 * calculation up to point i has already been performed.
	 *
	 * Each thread compares array a to orig, if a[i] != orig[i] it
	 * can be assumed that a[i] contains the prefix sum up to point i,
	 * so we update.
	 */
	pthread_t threads[THREAD_NUM];
	pprefix_args_t args[THREAD_NUM];

	size_t numbytes = sizeof(double*) * count;
	size_t thread_num = 0;

	double *orig = malloc(numbytes);
	memcpy(orig, a, numbytes);

	for (int i = 0; i < count; i ++) {

		/* We select the smallest of THREAD_NUM and the remaining
		 * elements to be processed
		 */
		thread_num = MIN(THREAD_NUM, count - i);

		for (int t = 0; t < thread_num; t++) {
			args[t].count = i;
			args[t].orig = orig;
			args[t].a = a;

			pthread_create(&threads[t], NULL, do_parallel_prefix_cb, &args[t]);
		}

		for (int t = 0; t < thread_num; t++) {
			pthread_join(threads[t], NULL);
		}

	}

}

void print_arr(double *a, int count)
{
	for (int i = 0; i < count; i++)
		printf("%f\n", a[i]);
}

double *read_lines(int *len)
{
	int curr_alloc = 256;
	double *ret = malloc(sizeof(double) * curr_alloc);
	char line[1024], *e;
	long v;

	*len = 0;
	while (fgets(line, sizeof(line), stdin)) {
		v = strtod(line, &e);
		if (line == e) {
			fprintf(stderr, "warning: Skipping line: %s", line);
			continue;
		}

		ret[*len] = v;
		(*len)++;

		if (*len == curr_alloc) {
			curr_alloc *= 2;
			ret = realloc(ret, curr_alloc);
		}

	}

	return ret;
}


int main(int argc, char* argv[])
{
	int len;
	double *a = read_lines(&len);

	parallelPrefixSum(a, len);

	print_arr(a, len);

	return EXIT_SUCCESS;
}
