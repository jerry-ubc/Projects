module ALU(Ain, Bin, ALUop, out, stat);
	input  [15:0] Ain, Bin;
	input  [1:0]  ALUop;
	output [15:0] out;
	output [2:0]  stat;
	// fill out the rest
	reg    [15:0] out;	
	reg    [2:0]  stat;
	wire          ovfAdd, ovfSub;
	wire   [15:0] sum, difference;

	AddSub #(16) addbackup(Ain, Bin, 1'b0, sum, ovfAdd);
	AddSub #(16) subbackup(Ain, Bin, 1'b1, difference, ovfSub);

	always @(*) begin
		//perform operation specified by ALUop
		case(ALUop)
			2'b00: out = sum;
			2'b01: out = difference;
			2'b10: out = Ain & Bin;
			2'b11: out = ~Bin;
			default: out = {16{1'bx}};
		endcase
		
		//zero flag
		if (out == {16{1'b0}})
			stat[0] = 1'b1;
		else
			stat[0] = 1'b0;

		//negative flag
		case(out[15])
			1'b0: stat[1] = 1'b0; 
			1'b1: stat[1] = 1'b1; 
			default: stat[1] = 1'bx;
		endcase

		//overflow flag
		casex(ALUop)
			2'b0x:
				if ((ovfAdd == 1'b1) || (ovfSub == 1'b1))
					stat[2] = 1;
				else 
					stat[2] = 0;
			2'b1x: stat[2] = 0;
			default: stat[2] = 1'bx;
		endcase
	end
endmodule

module Adder2(a, b, cin, cout, s);
	parameter n = 8;
	input [n-1:0] a, b;
	input cin;
	output [n-1:0] s;
	output cout;

	wire [n-1:0] p = a ^ b;
	wire [n-1:0] g = a & b;
	wire [n:0]   c = {g | (p & c[n-1:0]), cin};
	wire [n-1:0] s = p ^ c[n-1:0];
	wire         cout = c[n];
endmodule

module AddSub(a, b, sub, s, ovf);
	parameter n = 8;
	input [n-1:0] a, b;
	input sub;
	output [n-1:0] s;
	output ovf;
	wire c1, c2;
	wire ovf = c1 ^ c2;

	Adder2 #(n-1) ai(a[n-2:0], b[n-2:0]^{n-1{sub}}, sub, c1, s[n-2:0]);
	Adder2 #(1)   as(a[n-1], b[n-1]^sub, c1, c2, s[n-1]);
endmodule
