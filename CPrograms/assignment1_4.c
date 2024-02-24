#include <stdio.h>

int main(void) {
	// Initialise integer variables
	// for the input to be read into
	int a; int b; int n;
	scanf("%d %d %d", &a, &b, &n);
	
	// Initialise remainder variable
	int remainder = a % b;
	
	// Iterate to n-1 (one before
	// desired digit)
	for (int i = 0; i < n-1; i++) {
		// Calculate new remainder
		remainder = (remainder * 10) % b;
		if (remainder == 0) break;
	}
	
	// Scale remainder by 10 then perform
	// integer division to find quotient
	int quotient = (remainder * 10) / b;
	
	printf("%d", quotient);
	
	return 0;
}
