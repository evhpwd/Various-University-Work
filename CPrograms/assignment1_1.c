#include <stdio.h>
#define PI 3.14

int main(void) {
	// Initialise integer variables to store the inputted
	// radii and floats to store the results
	int radius1; int radius2;
	float sum_of_areas = 0; float sum_of_circumferences = 0;
	
	scanf("%d %d", &radius1, &radius2);
	
	// Iterate through each radius from radius1 to radius2 and add
	// the calculated area and circumference each time
	int next_radius = radius1;
	while (next_radius <= radius2) {
		sum_of_areas += PI * next_radius * next_radius;
		sum_of_circumferences += 2 * PI * next_radius;
		next_radius += 1;
	}
	
	// Print to 3 decimal places
	printf("%.3f\n%.3f", sum_of_areas, sum_of_circumferences);
	
	return 0;
}
