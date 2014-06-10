

public class Test {
	public static void main(String[] args){
		ALU alu=new ALU();
		System.out.println(alu.FloatRepresentation("0.625", 23, 8));
		System.out.println(alu.FloatRepresentation("0.8125", 23, 8));
		//System.out.println(alu.FloatRepresentation("0.5", 23, 8));
		//System.out.println(alu.FloatRepresentation("0.4375", 23, 8));
		//System.out.println(alu.FloatRepresentation("0.9375", 23, 8));
		System.out.println(alu.FloatRepresentation("-0.1875", 23, 8));
		//System.out.println(alu.FloatRepresentation("0.9375", 23, 8));
		//System.out.println(alu.FloatRepresentation("0.5", 23, 8));
		//System.out.println(alu.IEEE754("1.4375", 32));
		int[] length={23,8};
		//System.out.println(alu.TrueValue("00111110111000000000000000000000", ALU.Type.FLOAT, length));
		System.out.println(alu.SubtractionF("00111111001000000000000000000000", "00111111010100000000000000000000", 23, 8, 4));
	}
	
}
