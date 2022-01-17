`define Wait 4'd0
`define Decode 4'd1
`define GetA 4'd2
`define GetB 4'd3
`define ADD 4'd4
`define MOV 4'd5
`define	CMP 4'd6
`define AND 4'd7
`define MVN 4'd8
`define WriteReg 4'd9
`define WriteImm 4'd10

module cpu(clk,reset,s,load,in,out,N,V,Z,w);
	input clk, reset, s, load;
	input [15:0] in;
	output [15:0] out;
	output N, V, Z, w;

	//internal signals between modules
	wire [15:0] irout, mdata, C, sximm8, sximm5;
	wire [7:0] PC;
	wire [2:0] writenum, readnum, opcode, nsel, stat_out;
	wire [1:0] op, ALUop, shift, vsel;
	wire loada, loadb, loadc, loads, asel, bsel, write;

	instructionRegister ir(in, load, irout, clk);
	
	instructionDecoder id(irout, nsel, opcode, op, shift, ALUop, sximm5, sximm8, readnum, writenum);

	FSM fsm(s, reset, clk, opcode, op, nsel, vsel, loada, loadb, loadc, loads, asel, bsel, write, w);

	datapath DP(clk, readnum, vsel, loada, loadb, shift, asel, bsel, ALUop, 
		loadc, loads, writenum, write, mdata, sximm8, PC, sximm5, stat_out, C);

	assign out = C;
	assign V = stat_out[2];
	assign N = stat_out[1];
	assign Z = stat_out[0];

endmodule


module instructionRegister(in, load, irout, clk);
	//external inputs
	input [15:0] in;
	input load, clk;

	output [15:0] irout;
	reg [15:0] irout;
	
	//if load = 1, irout = in, otherwise irout does not change
	always @(posedge clk) begin
		irout = load ? in : irout;
	end
endmodule


module instructionDecoder(irout, nsel, opcode, op, shift, ALUop, sximm5, sximm8, readnum, writenum);
	//inputs from instructionRegister
	input [15:0] irout;
	input [2:0] nsel; //one-hot select for MUX

	output [2:0] opcode;
	output [1:0] op, shift, ALUop;
	output [15:0] sximm5, sximm8;
	output [2:0] readnum, writenum;
	reg [2:0] readnum;
	wire [4:0] imm5;
	wire [7:0] imm8;
	wire [2:0] Rn, Rd, Rm;
	
	//internal variables
	assign imm5 = irout [4:0];
	assign imm8 = irout [7:0];
	assign Rn = irout [10:8];
	assign Rd = irout [7:5];
	assign Rm = irout [2:0];

	//output variables
	assign ALUop = irout [12:11];
	assign sximm5 = {{11{irout[4]}}, imm5};
	assign sximm8 = {{8{irout[7]}}, imm8};
	assign shift = irout [4:3];
	assign opcode = irout [15:13];
	assign op = irout [12:11];

	//Mux to choose which register readnum gets
	always @(*) begin
		case(nsel)
			3'b100: readnum = Rn;
			3'b010: readnum = Rd;
			3'b001: readnum = Rm;
			default: readnum = {3{1'bx}};
		endcase
	end
	assign writenum = readnum;
endmodule


module FSM(s, reset, clk, opcode, op, nsel, vsel, loada, loadb, loadc, loads, asel, bsel, write, w);
	//external inputs
	input s, reset, clk;
	
	//inputs from instructionDecoder
	input [2:0] opcode;
	input [1:0] op;
	
	//internal outputs (to other logic blocks)
	output [2:0] nsel;
	output [1:0] vsel;
	output loada, loadb, loadc, loads, asel, bsel, write;
	
	//external outputs
	output w;
	
	reg [3:0] curState;
	reg [2:0] nsel;
	reg [1:0] vsel;
	reg w, loada, loadb, loadc, loads, asel, bsel, write;


	always @(posedge clk) begin
		if(reset) begin
			nsel = 3'd0;
			vsel = 2'd0;
			asel = 0;
			bsel = 0;
			loada = 0;
			loadb = 0;
			loadc = 0;
			loads = 0;
			write = 0;
			w = 1'b1;
			curState = `Wait;
		end else begin
			case(curState)
				`Wait: begin
					//nsel = 3'd0;
					vsel = 2'd0;
					asel = 0;
					bsel = 0;
					loada = 0;
					loadb = 0;
					loadc = 0;
					loads = 0;
					write = 0;
					if(s) begin
						curState = `Decode;
						w = 1'b0;
					end else begin
						curState = `Wait;
						w = 1;
					end
				end
				`Decode: begin
					if({opcode, op} == 5'b11010)
						curState = `WriteImm;
					else if({opcode, op} == 5'b10110 | {opcode, op} == 5'b10101 | {opcode, op} == 5'b10100) begin //GetA if ADD, CMP, or AND
						curState = `GetA;
					end else
						curState = `GetB;
				end
				`GetA: begin
					nsel = 3'b100; //A stored in Rn
					loada = 1'b1;
					curState = `GetB;
				end
				`GetB: begin
					nsel = 3'b001; //B stored in Rm
					loadb = 1'b1;
					loada = 1'b0;
					if({opcode, op} == 5'b11000)
						curState = `MOV;
					else if({opcode, op} == 5'b10100)
						curState = `ADD;
					else if({opcode, op} == 5'b10101)
						curState = `CMP;
					else if({opcode, op} == 5'b10110)
						curState = `AND;
					else
						curState = `MVN;
				end
				`ADD: begin
					asel = 0; //choose A instead of default input
					bsel = 0; //choose B instead of default input
					loadb = 0;
					loadc = 1;
					curState = `WriteReg;
				end
				`MOV: begin
					asel = 1; //choose 16'd0 instead of A
					bsel = 0; //choose B
					loadb = 0;
					loadc = 1;
					curState = `WriteReg;
				end
				`CMP: begin
					asel = 0;
					bsel = 0;
					loadb = 0;
					loadc = 1;
					loads = 1;//double check if this should be here
					curState = `WriteReg;
				end
				`AND: begin
					asel = 0;
					bsel = 0;
					loadb = 0;
					loadc = 1;
					curState = `WriteReg;
				end
				`MVN: begin
					asel = 1; //choose 16'd0 instead of A
					bsel = 0;
					loadb = 0;
					loadc = 1;
					curState = `WriteReg;
				end
				`WriteReg: begin
					write = 1; //writing to reg, so indicate intention
					vsel = 2'd0; //choose datapath_out from Lab 5
					nsel = 3'b010; //selects Rd to write
					//loads = 0;
					curState = `Wait;
				end
				`WriteImm: begin
					write = 1; //indicate writing
					vsel = 2'b10; //choose sximm8
					nsel = 3'b100; //selects Rn to write
					//loads = 0;
					curState = `Wait;
				end
				default: curState = 4'bxxxx;
			endcase
		end
	end
	
endmodule
