#!/usr/bin/python2
# Creates known-good test cases for a matrix multiplication program
import argparse
import os

import numpy as np


def write_matrix(name, m):
    with open(name, 'w') as f:
        f.write('%d %d\n' % (m.shape[0], m.shape[1]))
        for i, _ in enumerate(m):
            for j, _ in enumerate(m[i]):
                f.write("%d " % m[i][j])
            f.write("\n")


def create(args):
    for i in range(args.test_cases):

        if args.matrix_size == 'RAND':
            rows1, cols1 = np.random.randint(1, 100)
            rows2 = cols1
            cols2 = np.random.randint(1, 100)
        else:
            rows1, cols1, rows2, cols2 = [args.matrix_size] * 4

        m1 = np.random.randint(0, 100, size=(rows1, cols1))
        m2 = np.random.randint(0, 100, size=(rows2, cols2))

        # Write out the matrix
        try:
            os.makedirs(args.outdir)
        except:
            pass

        write_matrix('{outdir}/testcase{num}_1'.format(outdir=args.outdir, num=i), m1)
        write_matrix('{outdir}/testcase{num}_2'.format(outdir=args.outdir, num=i), m1)
        write_matrix('{outdir}/result{num}'.format(outdir=args.outdir, num=i), np.dot(m1, m2))


def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--test-cases', '-c', default=10, type=int, help='Number of test cases to generate')
    parser.add_argument('--matrix-size', '-m', default='100', help='Matrix size or RAND')
    parser.add_argument('--outdir', default='tests/', help='Output directory')

    args = parser.parse_args()
    if args.matrix_size != 'RAND':
        try:
            args.matrix_size = int(args.matrix_size)
        except ValueError:
            parser.error('Invalid matrix-size arguments. Expect: RAND or int')

    return args


def main():
    args = parse_args()
    create(args)


if __name__ == '__main__':
    main()
