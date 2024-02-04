#include <stdio.h>
#include <stdlib.h>

int main(void) {
	//Variables to contain counts, strings and inputs
	int poscount = 0;
	int negcount = 0;
	int zercount = 0;
	char howmany[] = "How many numbers: ";
	char inp[] = " %d";
	int n;
	char enternum[] = "\nEnter a number: ";
	int num;
	char posresult[] = "\n--------------" \
		"\nPositive: %4d";
	char negresult[] = "\nNegative: %4d";
	char zerresult[] = "\nZero: %4d";

	_asm {
		//Segment of code to ask the user how many numbers they want to input, stores the result in a variable
			lea eax, howmany
			push eax
			call printf
			pop eax
			lea eax, n
			push eax
			lea eax, inp
			push eax
			call scanf
			add esp, 8
		//Copies the input to the ecx and checks if it is less than/equal to 0, if so skips the loop
			mov ecx, n
			cmp ecx, 0
			jle none
		cont :
		//Asks the user for a number, stores the result in a variable
			push ecx
			lea eax, enternum
			push eax
			call printf
			pop eax
			lea eax, num
			push eax
			lea eax, inp
			push eax
			call scanf
			add esp, 8
			pop ecx
		//Compares the number to 0, increments the relevant counter depending on the result
			mov eax, num
			cmp eax, 0	
			jg greater
			je zero
		smaller :
			inc negcount
			jmp endif
		greater :
			inc poscount
			jmp endif
		zero :
			inc zercount
		endif :
		//Decrements counter and jumps back if the counter hasn't reached zero
			loop cont

		none :
		//Outputs each counter at the end of the loop
			push poscount
			lea eax, posresult
			push eax
			call printf
			add esp, 8
			push negcount
			lea eax, negresult
			push eax
			call printf
			add esp, 8
			push zercount
			lea eax, zerresult
			push eax
			call printf
			add esp, 8
	}
}