module cpu_tb();
	reg s, reset, clk, load, err;
	reg [15:0] in;
	wire [15:0] out;
	wire w,N,V,Z;
	
	cpu DUT(
	.clk(clk),
	.reset(reset),
	.s(s),
	.load(load),
	.in(in),
	.out(out),
	.N(N),
	.V(V),
	.Z(Z),
	.w(w));

	initial begin
    		$display("started");
    		clk = 0; #5;
    		forever begin
      			clk = 1; #5;
      			clk = 0; #5;
    		end
 	end


	initial begin
		err = 0; reset = 1; load = 0; in = 16'd0; s = 0; #10;
		reset = 0; #1;

		//MOV R2, #20
		in = 16'b1101001000010100; load = 1; #10; //load = 1 to load value on R2
		load = 1; #10;
		load = 0; s = 1; #10; //set s = 1 to indicate leaving `Wait
		s = 0;
		#25;
		$display("Register 2 contains %b", cpu_tb.DUT.DP.REGFILE.R2);
		if (cpu_tb.DUT.DP.REGFILE.R2 !== 16'd20) begin
      			err = 1;
      			$display("error R2: expected 00010100, got %b", cpu_tb.DUT.DP.REGFILE.R2);
    		end

		if(err)
			$display("errors");
		else
			$display("no errors!");

		$stop;
	end
endmodule
