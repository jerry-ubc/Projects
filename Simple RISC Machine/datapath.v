module datapath(clk, readnum, vsel, loada, loadb, shift, asel, bsel, ALUop, 
		loadc, loads, writenum, write, mdata, sximm8, PC, sximm5, stat_out, C);
	
	input         clk, write, loada, loadb, asel, bsel, loadc, loads;
	input  [15:0] mdata, sximm8, sximm5;
	input  [7:0]  PC;
	input  [2:0]  readnum, writenum;
	input  [1:0]  shift, ALUop, vsel;
	output [2:0]  stat_out;
	output [15:0] C;
	wire   [15:0] data_in, data_out, Ain, Bin, Aout, Bout, out, sout, extendPC;
	wire   [2:0]  stat;
	
	//assign 0 to mdata and PC (LAB6 ONLY)
	assign PC = 0;
	assign mdata = 0;
	assign extendPC = {8'b0, PC};

	//choose value of data_in
	//old code: assign data_in  = vsel ? datapath_in : datapath_out;
	mux4 mux(mdata, sximm8, extendPC, C, vsel, data_in);
	
	//write data_in into one of the registers R read from one of the registers
	regfile REGFILE(data_in, writenum, write, readnum, clk, data_out);

	//store data_out into regA/regB 
	loadEnable #(16) loadA(data_out, Aout, loada, clk);
	loadEnable #(16) loadB(data_out, Bout, loadb, clk);

	//shift value in regB
	shifter shifter(Bout, shift, sout);

	//choose value of Ain and Bin
	assign Ain = asel ? 16'b0 : Aout;
	assign Bin = bsel ? sximm5 : sout;

	//perform ALU operation and store value in out
	//put status in stat
	ALU alu(Ain, Bin, ALUop, out, stat);

	//determine C and stat_out
	loadEnable #(16) loadC(out, C, loadc, clk);
	loadEnable #(3) status(stat, stat_out, loads, clk);
endmodule

module mux4(a3, a2, a1, a0, s, out);
	input  [15:0] a3, a2, a1, a0; //inputs
	input  [1:0]  s; //binary select
	output [15:0] out;
	reg    [15:0] out;

	//choose value of out based on s
	always @(*) begin
		case(s)
			2'b00: out = a0;
			2'b01: out = a1;
			2'b10: out = a2;
			2'b11: out = a3;
			default: out = {16{1'bx}};
		endcase
	end
endmodule
