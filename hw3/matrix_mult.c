#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

size_t MAX_LINE_LENGTH = 4096;

typedef struct {
	int **matrix;
	uint32_t rows;
	uint32_t cols;
} matrix_t;

void usage(int exit_status)
{
	fprintf(stderr, "Usage: matrix_mult mfile1 mfile2 NUM_THREADS\n");
	exit(exit_status);
}

FILE *fopen_or_fail(const char *path)
{
	FILE *f = fopen(path, "r");
	int err = errno;
	if (f == NULL) {
		fprintf(stderr, "Error opening %s. %s", path, strerror(err));
		exit(err);
	}
	return f;
}

void get_row_cols(char *line, uint32_t *rows, uint32_t *cols)
{
	char *token;

	token = strsep(&line, " ");
	*rows = strtol(token, NULL, 10);
	token = strsep(&line, " ");
	*cols = strtol(token, NULL, 10);
}

matrix_t *file_to_matrix(FILE *f)
{
	size_t line_length = MAX_LINE_LENGTH;
	char *line[MAX_LINE_LENGTH];
	char *token;

	matrix_t *ret = malloc(sizeof(matrix_t));

	getline(line, &line_length, f);

	get_row_cols(*line, &ret->rows, &ret->cols);

	ret->matrix = malloc(ret->rows * sizeof(int));

	for (int i = 0; i < ret->rows; i++) {
		ret->matrix[i] = malloc(ret->cols * sizeof(int));
		getline(line, &line_length, f);
		for (int j = 0; j < ret->cols; j++) {
			token = strsep(line, " ");
			ret->matrix[i][j] = strtol(token, NULL, 10);
		}
	}

	return ret;
}

void print_matrix(matrix_t *m)
{
	for (int i = 0; i < m->rows; i++) {
		for (int j = 0; j < m->cols; j++) {
			printf("%d ", m->matrix[i][j]);
		}
		printf("\n");
	}
}

int main(int argc, char* argv[])
{
	if (argc != 4)
		usage(EXIT_FAILURE);

	char *matrix_path1 = argv[1];
	char *matrix_path2 = argv[2];
	long int num_threads = strtol(argv[3], NULL, 0);

	if (errno == ERANGE)
		usage(EXIT_FAILURE);

	
	FILE *m1f = fopen_or_fail(matrix_path1);
	FILE *m2f = fopen_or_fail(matrix_path2);

	matrix_t *m1 = file_to_matrix(m1f);
	matrix_t *m2 = file_to_matrix(m2f);

	print_matrix(m1);
	print_matrix(m2);


	return EXIT_SUCCESS;
}
