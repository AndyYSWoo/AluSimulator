
public class TestDrive {
	public static void main(String[] args){
		ALU alu=new ALU();
		//System.out.println(alu.LeftShift("01000110", 1));
		//System.out.println(alu.RightShift("11000110", 1));
		//System.out.println(alu.Complement("-7", 8));
		//System.out.println(alu.Negation("11111110"));
		//System.out.println(alu.TrueValue("11100000"));
		//System.out.println(alu.FullAdder('1', '1', '1'));
		//System.out.println(alu.CLAAdder("0110", "0110", '0', 8));
		System.out.println(alu.Addition("0110", "0110", '0', 8));
		//System.out.println(alu.Subtraction("0000", "0010", 8));
		//System.out.println(alu.Multiplication("1101", "1110", 8));
	}
}
