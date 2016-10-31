#include <errno.h>
#include <limits.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <time.h>

#include <omp.h>

#define DEFAULT_S 8000;
#define THREAD_POOL_SIZE 128

static pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
static int C = 0;

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
	unsigned int x = (RAND_MAX + 1u) / n;
	unsigned int y = x * n;
	unsigned int r;
	do {
		r = rand();
	} while(r >= y);
	return r / x;
}

void *do_montecarlo(void *result)
{
	static int R = 2000;

	int x, y;
	x = randint(R);
	y = randint(R);
	if (x * x + y * y < R * R) {
		pthread_mutex_lock(&mutex);
		C++;
		pthread_mutex_unlock(&mutex);
	}
	return NULL;
}

double MonteCarloPi(int s)
{
	size_t num_threads = 0;
	int spawned_threads = 0;
	double res;

	srand(time(NULL));

	pthread_t threads[THREAD_POOL_SIZE];

	while ( spawned_threads != s ) {

		if (s - spawned_threads < THREAD_POOL_SIZE)
			num_threads = s - spawned_threads;
		else
			num_threads = THREAD_POOL_SIZE;

		for (int t = 0; t < num_threads; t++) {
			pthread_create(&threads[t], NULL, &do_montecarlo, NULL);
		}

		for (int t = 0; t < num_threads; t++) {
			pthread_join(threads[t], NULL);
		}
		spawned_threads += num_threads;
	}

	fprintf(stderr, "c=%d\n", C);
	fprintf(stderr, "s=%d\n", s);
	res = (4 * C) / (double)s;

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
