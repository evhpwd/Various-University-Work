#include <stdio.h>
// Define constant LENGTH for amount of numbers in input
#define LENGTH 10

int main(void) {
	// Initialise variables to store input array,
	// address of smallest value, absolute value
	// of smallest value and current value
	int numbers[LENGTH];
	int* min_address;
	int min_num;
	int current_num;
	
	// Iterate for number of times equal to input length
	for (int i = 0; i < LENGTH; i++) {
		// Read new value into array
		scanf("%d", &numbers[i]);
		// Store current value
		current_num = numbers[i];
		
		// If it is negative, convert to positive
		// (absolute value)
		if (current_num < 0) {
			current_num *= -1;
		}
		// If it is the first input read, set
		// minimum address and values to it
		if (i == 0) {
			min_address = &numbers[i];
			min_num = current_num;
		}
		// Otherwise, if the absolute value
		// is less than the current minimum
		else if (current_num < min_num) {
			// Set minimum address and value
			// to current value's
			min_address = &numbers[i];
			min_num = current_num;
		}
	}
	
	// Set minimum value to value at minimum address
	// to account for negatives
	min_num = *min_address;
	// Swap values at last and minimum addresses
	*min_address = numbers[LENGTH - 1];
	numbers[LENGTH - 1] = min_num;
	
	// Print each value in the array
	for (int i = 0; i < LENGTH; i++) {
		printf("%d ", numbers[i]);
	}
	
	return 0;
}
