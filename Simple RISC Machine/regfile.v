module regfile(data_in, writenum, write, readnum, clk, data_out);
	input  [15:0] data_in;
	input  [2:0]  writenum, readnum;
	input         write, clk;
	output [15:0] data_out;
	// fill out the rest
	wire   [7:0]  writenumOneHot;
	wire   [15:0] R0, R1, R2, R3, R4, R5, R6, R7;
	reg    [15:0] data_out;

	decoder #(3,8) dec(writenum, writenumOneHot);

	//writing
	loadEnable #(16) r0(data_in, R0, writenumOneHot[0] & write, clk);
	loadEnable #(16) r1(data_in, R1, writenumOneHot[1] & write, clk);
	loadEnable #(16) r2(data_in, R2, writenumOneHot[2] & write, clk);
	loadEnable #(16) r3(data_in, R3, writenumOneHot[3] & write, clk);
	loadEnable #(16) r4(data_in, R4, writenumOneHot[4] & write, clk);
	loadEnable #(16) r5(data_in, R5, writenumOneHot[5] & write, clk);
	loadEnable #(16) r6(data_in, R6, writenumOneHot[6] & write, clk);
	loadEnable #(16) r7(data_in, R7, writenumOneHot[7] & write, clk);
	
	//reading
	always @(*) begin
		case(readnum)
			3'b000: data_out = R0;
			3'b001: data_out = R1;
			3'b010: data_out = R2;
			3'b011: data_out = R3;
			3'b100: data_out = R4;
			3'b101: data_out = R5;
			3'b110: data_out = R6;
			3'b111: data_out = R7;
			default: data_out = {15{1'bx}};
		endcase
	end
endmodule

module loadEnable(in, out, load, clk);
	parameter n = 16;	
	input  [n-1:0] in;
	input         load, clk;
	output [n-1:0] out;
	reg    [n-1:0] out;

	always @(posedge clk) begin
		out = load ? in : out;
	end
endmodule

module decoder(a, b);
	parameter n = 3;
	parameter m = 8;

	input  [n-1:0] a;
	output [m-1:0] b;

	wire [m-1:0] b = 1 << a;
endmodule



	