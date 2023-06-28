# Mystery Lottery Game

[![NOTHING2LOSE DEMO](http://img.youtube.com/vi/UfifT17lb00/0.jpg)](https://www.youtube.com/watch?v=UfifT17lb00 "Watch on Youtube")

This project aims to develop a system that generates four encrypted lottery tickets with hidden prizes and allows users to decipher one of those tickets. The encryption techniques employed in this system create exciting mystery games with challenging puzzles. The four tickets should have different values (e.g., a simple prize of 1 rune, a medium prize of 2 runes, a rare prize of 4 runes, and a legendary prize of 8 runes) and should incrementally become more difficult to open. In other words, if a user selects a ticket with a higher prize (unaware of the value), it should take more time to decipher (e.g., the ticket with a simple prize should take 30 seconds, while the medium prize should take double that time, i.e., 60 seconds, and the legendary prize should take 4 minutes). The system to be built should simulate the following steps:

1. The system generates four tickets with different prizes (the prizes should be random but defined by an exponential distribution).
2. The system generates four encryption keys with varying complexity (e.g., the key for the simple prize has 20 random bits + 108 bits filled with zeros, the key for the medium prize has 21 random bits + 107 bits filled with zeros, the key for the rare prize has 22 random bits + 106 bits filled with zeros, and the key for the legendary prize has 23 random bits + 105 bits filled with zeros).
3. The system calculates message authentication codes for the various prizes.
4. The system encrypts the four tickets.
5. The system prompts the user to choose a prize and initiates the decryption process for that prize until the result is revealed to the user (the user must wait until they receive the prize or give up and choose another one).

## Basic Features

The system should support the following basic functionalities:

1. Simple user registration using an email and password (a secure representation of the password should be stored in the database).
2. Generation of tickets with different prizes according to the specified scheme.
3. Generation of encryption keys with varying complexity to ensure decryption times of 30, 60, 120, and 240 seconds (test the complexity levels to determine the appropriate number of bits).
4. Calculation of message authentication codes using HMAC-SHA256.
5. Encryption of tickets using the generated keys and AES-128-CBC.
6. Allow registered users to select a ticket and attempt to decrypt it (taking the necessary time) while displaying the result at the end.
7. Enable users to give up on decrypting a ticket if it takes too long and choose another one until no tickets are left.

## Additional Features

To further enhance the system and expand its capabilities, consider implementing the following functionalities:

1. Ability to choose between AES-128-CBC and AES-128-CTR ciphers.
2. Option to select the HMAC function between HMAC-SHA256 and HMAC-SHA512.
3. In addition to using message authentication codes to verify successful decryption, the system incorporates RSA digital signatures (the system should be able to generate an RSA key pair).
4. Invent intelligent decryption assistance schemes (e.g., correctly answering a question reduces the decryption time by revealing a portion of the encryption key).

## Getting Started

To install the application, please download the file `nothing2lose.apk` and proceed to run it on your Android device.
