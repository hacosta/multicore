#include <errno.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <time.h>

#include <omp.h>

static int R = 4000;
#define DEFAULT_S 8000;

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

double MonteCarloPi(int s)
{
	int R2 = R * R;
	int c = 0;
	double res;
	int x, y, x2, y2;

	srand(time(NULL));
#pragma omp parallel for reduction (+:c) shared(x, y, x2, y2, R)
	for (int i = 0; i < s; i++) {
		x = randint(R);
		y = randint(R);
		x2 = x * x;
		y2 = y * y;
		if (x2 + y2 < R2)
			c++;
	}

	fprintf(stderr, "c=%d\n", c);
	fprintf(stderr, "s=%d\n", s);
	res = (4 * c) / (double)s;

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
