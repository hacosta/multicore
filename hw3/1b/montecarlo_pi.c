#include <errno.h>
#include <limits.h>
#include <pthread.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <time.h>

#define DEFAULT_S 51200000
#define THREAD_POOL_SIZE 4

void usage(int exit_status)
{
	fprintf(stderr, "Usage: montecarlo_pi [s]\n");
	exit(exit_status);
}

int randint(int max, unsigned int *seed){
	/* Adapted from http://c-faq.com/lib/randrange.html
	 * we assume lowerbound min to be 0 to simplify
	 * the function call and use rand_r instead of
	 * rand() which is *much* slower in multi-threaded
	 * applications
	 */
	return rand_r(seed) / (RAND_MAX / (max + 1) + 1);
}

void *do_montecarlo(void *s)
{
	static int R = 12000;

	int x, y, x2, y2;
	int *c = malloc(sizeof(int *));
	int R2 = R * R;
	int casted_s = *(int *)s;

	srand(time(NULL));
	unsigned int seed = rand();

	*c = 0;
	for (int i = 0; i < casted_s; i ++) {
		x = randint(R, &seed);
		y = randint(R, &seed);
		x2 = x * x;
		y2 = y * y;
		if (x2 + y2 < R2) {
			*c = *c + 1;
		}
	}

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

	for (int t = 0; t < THREAD_POOL_SIZE; t++) {
		pthread_join(threads[t], (void **)&c);
		c_accum += *c;
	}

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
