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
#define DEFAULT_S 512000000

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

int do_montecarlo(int s)
{
	static int R = 8932;

	int x, y, x2, y2;
	int ret = 0;
	int R2 = R * R;

	srand(time(NULL));
	unsigned int seed = rand();

#pragma omp parallel
{
#pragma omp for
	for (int i = 0; i < s; i ++) {
		x = randint(R, &seed);
		y = randint(R, &seed);
		x2 = x * x;
		y2 = y * y;
		if (x2 + y2 < R2) {
#pragma omp critical
			ret++;
		}
	}
}
	return ret;
}

double MonteCarloPi(int s)
{
	int c;
	double res;

	c = do_montecarlo(s);
	fprintf(stderr, "c=%d\n", c);
	fprintf(stderr, "s=%d\n", s);
	res = 4.0 * (double)c / (double)s;

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
