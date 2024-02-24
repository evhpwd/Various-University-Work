#include <stdio.h>

// Function to find the length of a string, takes a
// pointer to the first character in an array as a
// parameter and returns an integer
int mystrlen(char* string) {
	// Initialises variables to store current character
	// and length
	char c = string[0];
	int len = 0;
	
	//Iterates until the end of the string is reached
	while (c != 0) {
		// Increments length and sets character to
		// next value in array
		len += 1;
		c = string[len];
	}
	
	return len;
}

int main(void) {
	// Initialise array of characters to store input
	char input[200] = { 0 };
	fgets(input, sizeof(input), stdin);
	
	// Calculate length of input and store
	int len = mystrlen(input);
	
	if (len == 1) {
		// If there is only 1 character, return it (no
		// reversal needed)
		printf("%c", input[0]);
	}
	else {
		// Iterates up to value preceding the halfway
		// point in the array
		for (int i = 0; i < len/2; i++) {
			/*
			Swaps the values at each point with
			 value equivalent distance from end
			 of array
			So first and last characters are
			 swapped, second and second last are
			 swapped, and so on
			*/
			char temp = input[i];
			input[i] = input[len-i-1];
			input[len-i-1] = temp;
		}
		printf("%s", input);
	}
	
	return 0;
}
