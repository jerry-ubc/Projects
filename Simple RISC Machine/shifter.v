module shifter(in,shift,sout);
	input [15:0] in;
	input [1:0] shift;
	output [15:0] sout;
	// fill out the rest
	reg [15:0] sout;
	
	//shift bits depending on 'shift' input
	always @(*) begin
		case(shift)
			2'b00: sout = in; 
			2'b01: sout = in << 1;
			2'b10: sout = in >> 1;
			2'b11: sout = {in[15],in[15:1]};
			default: sout = {16{1'bx}};
		endcase
	end
endmodule