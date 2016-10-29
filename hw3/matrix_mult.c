#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#include <omp.h>

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
		fprintf(stderr, "matrix_mult: Error opening %s. %s\n", path, strerror(err));
		exit(err);
	}
	return f;
}

matrix_t *init_matrix(matrix_t *mat, uint32_t rows, uint32_t cols)
{
	mat->rows = rows;
	mat->cols = cols;

	mat->matrix = malloc(rows * sizeof(int*));
	for (int i = 0; i < rows; i++) {
		mat->matrix[i] = malloc(cols * sizeof(int));
	}

	return mat;
}

void get_row_cols(char *line, uint32_t *rows, uint32_t *cols)
{
	char *token;

	token = strsep(&line, " ");
	*rows = strtol(token, NULL, 10);
	token = strsep(&line, " ");
	*cols = strtol(token, NULL, 10);
}

int file_to_matrix(FILE *f, matrix_t *ret)
{
	size_t line_length = MAX_LINE_LENGTH;
	char *line[MAX_LINE_LENGTH];
	char *token;
	uint32_t rows, cols;

	getline(line, &line_length, f);

	get_row_cols(*line, &rows, &cols);

	init_matrix(ret, rows, cols);

	for (int i = 0; i < ret->rows; i++) {
		getline(line, &line_length, f);
		for (int j = 0; j < ret->cols; j++) {
			token = strtok(j == 0 ? *line : NULL, " ");
			ret->matrix[i][j] = strtol(token, NULL, 10);
		}
	}

	return 0;
}

void print_matrix(matrix_t *m)
{
	printf("%d %d\n", m->rows, m->cols);
	for (int i = 0; i < m->rows; i++) {
		for (int j = 0; j < m->cols; j++) {
			printf("%d ", m->matrix[i][j]);
		}
		printf("\n");
	}
}

matrix_t *matrix_multiply(const matrix_t *m1, const matrix_t *m2, matrix_t *result)
{
	if (m1->cols != m2->rows)
		return NULL;

	init_matrix(result, m1->rows, m2->cols);
	int i, j, k;

	fprintf(stderr, "matrix_mult: Using %d threads\n", omp_get_max_threads());

#pragma omp parallel for private (i, j, k) shared(result)
	for (i = 0; i < m1->rows; i++)
		for (j = 0; j < m2->cols; j++)
			for (k = 0; k < m1->cols; k++)
				result->matrix[i][j] += m1->matrix[i][k] * m2->matrix[k][j];

	return result;
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


	omp_set_num_threads(num_threads);

	matrix_t m1, m2, result;

	file_to_matrix(m1f, &m1);
	file_to_matrix(m2f, &m2);

	matrix_multiply(&m1, &m2, &result);

	print_matrix(&result);

	return EXIT_SUCCESS;
}
