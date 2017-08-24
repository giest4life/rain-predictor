#!/usr/bin/env python3
"""This is a simple script to take list of plain text passwords
and encrypt them using bcrypt"""
import csv
import bcrypt

HASHED_USER_DATA = 'users.csv'
WORKRATE = 12
MAX_ROWS = 200

with open('MOCK_DATA.csv') as csvfile:
    READER = csv.DictReader(csvfile)
    with open(HASHED_USER_DATA, 'w') as csvwritefile:
        WRITER = csv.DictWriter(csvwritefile, ['email', 'password'])
        WRITER.writeheader()
        N = 1
        for row in READER:
            if N > MAX_ROWS:
                break
            print('Hashing password on row {:d}'.format(N))
            password = row['password']
            hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt(WORKRATE))
            WRITER.writerow({'email': row['email'], 'password': hashed.decode()})
            N += 1

    print("Finished hashing all passwords")
