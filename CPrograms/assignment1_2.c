#include <stdio.h>

int main(void) {
	// Initialise variables to store the counts for each type of character
	int number_of_english_characters = 0;
	int number_of_digits = 0;
	int number_of_spaces = 0;
	int number_of_other_characters = 0;
	
	// Initialise a variable to store the current character
	char character;
	
	// Loop until the end of the string is reached
	while (scanf("%c", &character) != EOF) {
		// Checks for each type of character and increments relevant count
		if ((character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z')) {
			number_of_english_characters += 1;
		}
		else if (character >= '0' && character <= '9') {
			number_of_digits += 1;
		}
		else if (character == ' ') {
			number_of_spaces += 1;
		}
		else if (character != '\n') {
			number_of_other_characters += 1;
		}
	}
	
	printf("%d %d %d %d\n", number_of_english_characters, number_of_digits, number_of_spaces, number_of_other_characters);
	
	return 0;
}
