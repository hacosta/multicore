#include <errno.h>
#include <limits.h>
#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <time.h>

#include <omp.h>

#define DEFAULT_S 8000;
#define THREAD_POOL_SIZE 2

void usage(int exit_status)
{
	fprintf(stderr, "Usage: montecarlo_pi [s]\n");
	exit(exit_status);
}

int randint(int n) {
	/* Taken from http://c-faq.com/lib/randrange.html,
	 * We can't simply use modulo here, because we'd
	 * skew the results, this seems good enough
	 */
	static bool initialized = false;
	static unsigned int rand_state = 0;

	if (! initialized) {
		srand(time(NULL));
		rand_state = rand();
		initialized = true;
	}

	unsigned int x = (RAND_MAX + 1u) / n;
	unsigned int y = x * n;
	unsigned int r;
	do {
		r = rand_r(&rand_state);
	} while(r >= y);
	return r / x;
}

void *do_montecarlo(void *s)
{
	static int R = 2000;

	int x, y;
	int *c = malloc(sizeof(int *));
	int R2 = R * R;
	int casted_s = *(int *)s;

	*c = 0;
	fprintf(stderr, "Casted s: %d\n", casted_s);
	for (int i = 0; i < casted_s; i ++) {
		x = randint(R);
		y = randint(R);
		if (x * x + y * y < R2)
			(*c)++;
	}

	fprintf(stderr, "Returning %d\n", *c);
	return (void*)c;
}

double MonteCarloPi(int s)
{
	int *c = malloc(sizeof(int*));
	int c_accum = 0;
	double res;

	pthread_t threads[THREAD_POOL_SIZE];
	int s_arr[THREAD_POOL_SIZE];

	for (int t = 0; t < THREAD_POOL_SIZE; t++) {
		s_arr[t] = s / THREAD_POOL_SIZE;

		if (t == THREAD_POOL_SIZE - 1) {
			/* Include the remaininder in the last thread*/
			s_arr[t] += (s % THREAD_POOL_SIZE);
		}

		pthread_create(&threads[t], NULL, &do_montecarlo, &s_arr[t]);
	}

	for(int i = 0; i < THREAD_POOL_SIZE; i++)
		fprintf(stderr, "arr: %d\n", s_arr[i]);

	for (int t = 0; t < THREAD_POOL_SIZE; t++) {
		pthread_join(threads[t], (void **)&c);
		c_accum += *c;
	}

	fprintf(stderr, "c=%d\n", c_accum);
	fprintf(stderr, "s=%d\n", s);
	res = (4 * c_accum) / (double)s;

	return res;
}

int main(int argc, char* argv[])
{
	int s;
	char *endptr;

	if (argc == 1) {
		s = DEFAULT_S;
	} else if (argc == 2) {
		s = strtol(argv[1], &endptr, 10);
		if (errno != 0 || endptr == argv[1]) {
			usage(EXIT_FAILURE);
		}
	} else {
		usage(EXIT_FAILURE);
	}
	
	printf("%f\n", MonteCarloPi(s));

	return EXIT_SUCCESS;
}
