import java.util.ArrayList;

/**
 * @author ������
 * @number 131250092
 */
public class ALU {
	public enum Operation {
		ADDITION, 
		SUBTRACTION, 
		MULTIPLICATION, 
		DIVISION
	};
	public enum Type {
		INTEGER, 
		FLOAT, 
		DECIMAL
	};
	public String Complement (String number, int length){
		char[] resultArray = new char[length];
		String result=null;
		int test=1;
		int num=Integer.parseInt(number);
		for(int i=0;i<length;i++){
			if((test&num)==0){
				resultArray[length-i-1]='0';
			}else{resultArray[length-i-1]='1';}
			test=test<<1;
		}
		result=String.valueOf(resultArray);

		return result;
	}
	public String TrueValue (String operand){
		char[] input;
		int resultNum=0;
		String result=null;
		input=operand.toCharArray();
		for(int i=operand.length()-1;i>0;i--){
			if(input[i]=='1'){
				resultNum=resultNum+(int)Math.pow(2,operand.length()-i-1);
			}else{}
		}
		if(input[0]=='1'){
			resultNum=resultNum-(int)Math.pow(2,operand.length()-1);
		}
		result=String.valueOf(resultNum);
		return result;
		
	}
	public String Negation (String operand){
		char[] temp;
		String result=null;
		temp=operand.toCharArray();
		for(int i=0;i<operand.length();i++){
			if(temp[i]=='0'){
				temp[i]='1';
			}else{temp[i]='0';}
		}
		result=String.valueOf(temp);
		return result;
	}
	public String LeftShift(String operand,int n){
		String result=null;
		char[] inputArray=operand.toCharArray();

		for(int i=0;i<=operand.length()-1-n;i++){
			inputArray[i]=inputArray[i+n];
		}
		for(int j=operand.length()-n;j<operand.length()-1;j++){
			inputArray[j]='0';
		}
		result=String.valueOf(inputArray);
		return result;
	}
	public String RightShift(String operand,int n){
		String result=null;
		char[] inputArray=operand.toCharArray();
	
		for(int i=operand.length()-1;i>=n;i--){
			inputArray[i]=inputArray[i-n];
		}
		for(int j=0;j<n;j++){
			inputArray[j]=inputArray[0];
		}
		result=String.valueOf(inputArray);
		return result;
	}
	public String FullAdder (char x, char y, char c){
		String result=null;
		String xs=String.valueOf(x);
		String ys=String.valueOf(y);
		String cs=String.valueOf(c);
		int xn=Integer.parseInt(xs);
		int yn=Integer.parseInt(ys);
		int cn=Integer.parseInt(cs);
		int sum=xn^yn^cn;
		int carryOut=(xn&yn)|(xn&cn)|(yn&cn);
		result=String.valueOf(sum)+String.valueOf(carryOut);
		return result;
	}
	public String CLAAdder (String operand1, String operand2, char c, int length){
		char[] o1=new char[length];
		char[] o2=new char[length];
		int[] p=new int[length];
		int[] g=new int[length];
		char[] carry=new char[length+1];
		char[] resultArray=new char[length+1];
		String result=null;
 		int c0=Integer.parseInt(String.valueOf(c));
		
		for(int i=0;i<operand1.length();i++){
			o1[length-1-i]=operand1.charAt(operand1.length()-i-1);
		}
		for(int j=0;j<=length-operand1.length();j++){
			o1[length-operand1.length()-j]=operand1.charAt(0);
		}

		for(int i=0;i<operand2.length();i++){
			o2[length-1-i]=operand2.charAt(operand2.length()-i-1);
		}
		for(int j=0;j<=length-operand2.length();j++){
			o2[length-operand2.length()-j]=operand2.charAt(0);
		}

		for(int i=0;i<length;i++){
			p[i]=Integer.parseInt(String.valueOf(o1[i]))|Integer.parseInt(String.valueOf(o2[i]));
			g[i]=Integer.parseInt(String.valueOf(o1[i]))&Integer.parseInt(String.valueOf(o2[i]));
		}

		carry[8]=c;
		carry[7]=String.valueOf((g[7]|(p[7]&c0))).charAt(0);
		carry[6]=String.valueOf((g[6]|(p[6]&g[7])|(p[6]&p[7]&c0))).charAt(0);
		carry[5]=String.valueOf((g[5]|(p[5]&g[6])|(p[5]&p[6]&g[7])|(p[5]&p[6]&p[7]&c0))).charAt(0);
		carry[4]=String.valueOf((g[4]|(p[4]&g[5])|(p[4]&p[5]&g[6])|(p[4]&p[5]&p[6]&g[7])|(p[4]&p[5]&p[6]&p[7]&c0))).charAt(0);
		carry[3]=String.valueOf((g[3]|(p[3]&g[4])|(p[3]&p[4]&g[5])|(p[3]&p[4]&p[5]&g[6])|(p[3]&p[4]&p[5]&p[6]&g[7])|(p[3]&p[4]&p[5]&p[6]&p[7]&c0))).charAt(0);
		carry[2]=String.valueOf((g[2]|(p[2]&g[3])|(p[2]&p[3]&g[4])|(p[2]&p[3]&p[4]&g[5])|(p[2]&p[3]&p[4]&p[5]&g[6])|(p[2]&p[3]&p[4]&p[5]&p[6]&g[7])|(p[2]&p[3]&p[4]&p[5]&p[6]&p[7]&c0))).charAt(0);
		carry[1]=String.valueOf((g[1]|(p[1]&g[2])|(p[1]&p[2]&g[3])|(p[1]&p[2]&p[3]&g[4])|(p[1]&p[2]&p[3]&p[4]&g[5])|(p[1]&p[2]&p[3]&p[4]&p[5]&g[6])|(p[1]&p[2]&p[3]&p[4]&p[5]&p[6]&g[7])|(p[1]&p[2]&p[3]&p[4]&p[5]&p[6]&p[7]&c0))).charAt(0);
		carry[0]=String.valueOf((g[0]|(p[0]&g[1])|(p[0]&p[1]&g[2])|(p[0]&p[1]&p[2]&g[3])|(p[0]&p[1]&p[2]&p[3]&g[4])|(p[0]&p[1]&p[2]&p[3]&p[4]&g[5])|(p[0]&p[1]&p[2]&p[3]&p[4]&p[5]&g[6])|(p[0]&p[1]&p[2]&p[3]&p[4]&p[5]&p[6]&g[0])|(p[0]&p[1]&p[2]&p[3]&p[4]&p[5]&p[6]&p[7]&c0))).charAt(0);

		for(int k=0;k<length;k++){
			resultArray[k]=FullAdder(o1[k],o2[k],carry[k+1]).charAt(0);
		}

		result=String.valueOf(resultArray)+carry[0];
		return result;
	}
	public String Addition (String operand1, String operand2, char c, int length){
		String result=null;
		
		//��������ת��Ϊlength����
		char[] o1=new char[length];
		char[] o2=new char[length];
		for(int i=0;i<operand1.length();i++){
			o1[length-1-i]=operand1.charAt(operand1.length()-i-1);
		}
		for(int j=0;j<=length-operand1.length();j++){
			o1[length-operand1.length()-j]=operand1.charAt(0);
		}

		for(int i=0;i<operand2.length();i++){
			o2[length-1-i]=operand2.charAt(operand2.length()-i-1);
		}
		for(int j=0;j<=length-operand2.length();j++){
			o2[length-operand2.length()-j]=operand2.charAt(0);
		}
		String op1=String.valueOf(o1);
		String op2=String.valueOf(o2);

		//���㰴λ������
		int flag=length/8;
		ArrayList<String> resultList=new ArrayList<String>();
		String result1=this.CLAAdder(op1, op2, c,8);
		resultList.add(result1);
		result=result1.substring(0,8);
		for(int i=1;i<flag;i++){
			resultList.add(this.CLAAdder(op1.substring(8*i,8*i+8), op2.substring(8*i,8*i+8), resultList.get(i-1).charAt(8), 8));
			result=resultList.get(i).substring(0,8)+result;
		}
		
		//�������λ
		int xn=Integer.parseInt(String.valueOf(operand1.charAt(0)));
		int yn=Integer.parseInt(String.valueOf(operand2.charAt(0)));
		int sn=Integer.parseInt(String.valueOf(result.charAt(0)));
		String overflow=String.valueOf((xn&yn&(1-sn))|((1-xn)&(1-yn)&sn));
		result=result+overflow;
		return result;
	}
	public String Subtraction (String operand1, String operand2, int length){
		String absOperand2=this.Negation(operand2);
		String result=null;
		result=this.Addition(operand1, absOperand2, '1', length);
		return result;
	}
	public String Multiplication (String operand1, String operand2, int length){
		return null;
	}
	public String Division (String operand1, String operand2, int length){
		return null;
	}
	public String Calculation (String number1, String number2,Type type, Operation operation, int length){
		return null;
	}

}