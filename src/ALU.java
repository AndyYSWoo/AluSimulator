import java.util.ArrayList;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Single;
import com.sun.xml.internal.xsom.impl.parser.SubstGroupBaseTypeRef;

/**
 * @author 吴永尚
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
	public String FloatRepresentation(String number, int sLength, int eLength){
		//denormalize,infinity to go
		String result=null;
		String tempResult=null;
		boolean isZero=true;
		
		String sign="0";
		String integer=null;
		String integerB=null;
		String fraction=null;
		String fractionB="";
		
		int exp=0;
		String exponent=null;
		String significant=null;
		
		if(number.charAt(0)=='-'){
			sign="1";
			number=number.substring(1);
		}
		for (int i = 0; i < number.length(); i++) {
			if(number.charAt(i)=='.'){
				integer=number.substring(0,i);
				fraction=number.substring(i+1);
			}
		}
		integerB=Complement(integer, sLength);
		double frac=Double.parseDouble("0."+fraction);

		for(int i=0;i<sLength;i++){
			if((frac=frac*2)>=1){
				fractionB=fractionB+"1";
				frac=frac-1;
			}else {
				fractionB=fractionB+"0";
			}
			
		}
		tempResult=integerB+fractionB;
		for(int i=0;i<tempResult.length();i++){
			if(tempResult.charAt(i)=='1'){
				isZero=false;
				exp=sLength-1-i;
				significant=tempResult.substring(i+1);
				
				if(significant.length()<sLength){
					int times=sLength-significant.length();
					for (int j = 0; j <times; j++) {
						significant=significant+"0";
					}
				}else if(significant.length()>sLength){
					significant=significant.substring(0,sLength);
				}
				break;
			}
		}
		exp=exp+(int)(Math.pow(2, eLength-1))-1;
		exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
		
		if(isZero){
			significant=tempResult.substring(0,sLength);
			exponent="0";
			for (int i = 1; i < eLength; i++) {
				exponent=exponent+"0";
			}
		}

		result=sign+exponent+significant;
		return result;
	}
	
	public String IEEE754(String number, int length){
		String result=null;
		int sLength=0;
		int eLength=0;
		if(length==32){
			sLength=23;
			eLength=8;
		}else if(length==64){
			sLength=52;
			eLength=11;
		}
		result=FloatRepresentation(number, sLength, eLength);
		return result;
	}
	
	public String TrueValue (String operand,Type type,int[] length){
		String result=null;
		
		switch (type) {
		case INTEGER:
			char[] input;
			int resultNum=0;
			
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
			break;
			
		case FLOAT:
			char sign=operand.charAt(0);
			String resultSign="";
			int sLength=length[0];
			int eLength=length[1];
			String exponent=operand.substring(1,eLength+1);
			String significant=operand.substring(eLength+1);
			boolean isExpOne=true;
			boolean isExpZero=true;
			boolean isSigZero=true;
			
			String integer=null;
			String fraction=null;
			int integerValue=0;
			double fracValue=0;
			
			if(sign=='1'){
				resultSign="-";
			}
			
			for(int i=0;i<eLength;i++){
				if(exponent.charAt(i)=='1'){isExpZero=false;}
				if(exponent.charAt(i)=='0'){isExpOne=false;}
			}
			for (int i = 0; i < sLength; i++) {
				if(significant.charAt(i)=='1'){isSigZero=false;}
				}
			
			if(isExpOne&&isSigZero&&sign=='0') {
				result="+Inf";
			}else if(isExpOne&&isSigZero&&sign=='1') {
				result="-Inf";
			}else if (isExpOne&&!isSigZero) {
				result="NaN";
			}else if(isExpZero&&isSigZero){
				result=resultSign+"0";
			}else {
				String temp;
				int exp;
				int bias;
				if(isExpZero&&!isSigZero) {
				//Denormalize
					temp="0"+significant;
					exp=0;
					bias=exp-((int)(Math.pow(2, eLength-1))-2);
				}else{
				//Normalize
					temp="1"+significant;
					exp=Integer.parseInt(TrueValue("0"+exponent, Type.INTEGER, length));
					bias=exp-((int)(Math.pow(2, eLength-1))-1);
				}
					if(bias>=0){
						integer=temp.substring(0,bias+1);
						fraction=temp.substring(bias+1);
						}else{
						String aidTmp="0";
						for (int i = 1; i < -bias; i++) {
							aidTmp=aidTmp+"0";
						}
						temp=aidTmp+temp;
						integer=temp.substring(0,1);
						fraction=temp.substring(1);
					}
					integerValue=Integer.parseInt(TrueValue("0"+integer, Type.INTEGER, length));
					for(int i=0;i<fraction.length();i++){
						if(fraction.charAt(i)=='1'){
							fracValue=fracValue+Math.pow(2, -(i+1));
						}
					}
					result=resultSign+String.valueOf(integerValue+fracValue);
					
				
				
			}

			
			
			

			break;
	}
		
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
	public String LogicRightShift(String operand, int n) {
		String result = "";
		int length = operand.length();
		for(int i = length - 1 - n; i >= 0; i--) {
			result = operand.charAt(i) + result;
		}
		for(int i = 0; i < n; i++) {
			result = '0' + result;
		}
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
		//Turn operands into required length:
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
			p[i]=Integer.parseInt(String.valueOf(o1[length-1-i]))|Integer.parseInt(String.valueOf(o2[length-1-i]));
			g[i]=Integer.parseInt(String.valueOf(o1[length-1-i]))&Integer.parseInt(String.valueOf(o2[length-1-i]));
		}

		carry[0]=c;
		switch(length){
		case 8:
			carry[8]=String.valueOf((g[7]|(p[7]&g[6])|(p[7]&p[6]&g[5])|(p[7]&p[6]&p[5]&g[4])|(p[7]&p[6]&p[5]&p[4]&g[3])|(p[7]&p[6]&p[5]&p[4]&p[3]&g[2])|(p[7]&p[6]&p[5]&p[4]&p[3]&p[2]&g[1])|(p[7]&p[6]&p[5]&p[4]&p[3]&p[2]&p[1]&g[0])|(p[7]&p[6]&p[5]&p[4]&p[3]&p[2]&p[1]&p[0]&c0))).charAt(0);
		case 7:
			carry[7]=String.valueOf((g[6]|(p[6]&g[5])|(p[6]&p[5]&g[4])|(p[6]&p[5]&p[4]&g[3])|(p[6]&p[5]&p[4]&p[3]&g[2])|(p[6]&p[5]&p[4]&p[3]&p[2]&g[1])|(p[6]&p[5]&p[4]&p[3]&p[2]&p[1]&g[0])|(p[6]&p[5]&p[4]&p[3]&p[2]&p[1]&p[0]&c0))).charAt(0);
		case 6:
			carry[6]=String.valueOf((g[5]|(p[5]&g[4])|(p[5]&p[4]&g[3])|(p[5]&p[4]&p[3]&g[2])|(p[5]&p[4]&p[3]&p[2]&g[1])|(p[5]&p[4]&p[3]&p[2]&p[1]&g[0])|(p[5]&p[4]&p[3]&p[2]&p[1]&p[0]&c0))).charAt(0);
		case 5:
			carry[5]=String.valueOf((g[4]|(p[4]&g[3])|(p[4]&p[3]&g[2])|(p[4]&p[3]&p[2]&g[1])|(p[4]&p[3]&p[2]&p[1]&g[0])|(p[4]&p[3]&p[2]&p[1]&p[0]&c0))).charAt(0);
		case 4:
			carry[4]=String.valueOf((g[3]|(p[3]&g[2])|(p[3]&p[2]&g[1])|(p[3]&p[2]&p[1]&g[0])|(p[3]&p[2]&p[1]&p[0]&c0))).charAt(0);
		case 3:
			carry[3]=String.valueOf((g[2]|(p[2]&g[1])|(p[2]&p[1]&g[0])|(p[2]&p[1]&p[0]&c0))).charAt(0);
		case 2:
			carry[2]=String.valueOf((g[1]|(p[1]&g[0])|(p[1]&p[0]&c0))).charAt(0);
		case 1:
			carry[1]=String.valueOf((g[0]|(p[0]&c0))).charAt(0);
		}
		for(int k=0;k<length;k++){
			resultArray[k]=FullAdder(o1[k],o2[k],carry[length-k-1]).charAt(0);
		}

		result=String.valueOf(resultArray)+carry[length-1];
		return result;
	}
	public String Addition (String operand1, String operand2, char c, int length){
		String result=null;
		
		//Turn operands into required length:
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
		//Calculate the result every 8-bit:
		int flag=length/8;
		ArrayList<String> resultList=new ArrayList<String>();
		String result1=this.CLAAdder(op1.substring(length-8), op2.substring(length-8), c,8);
		resultList.add(result1);
		result=result1.substring(0,8);
		for(int i=1;i<flag;i++){

			resultList.add(this.CLAAdder(op1.substring(length-(8*i+8),length-8*i), op2.substring(length-(8*i+8),length-8*i), resultList.get(i-1).charAt(9), 8));
			result=resultList.get(i).substring(0,8)+result;
		}
		
		//Calculate the overflow:
		int xn=Integer.parseInt(String.valueOf(operand1.charAt(0)));
		int yn=Integer.parseInt(String.valueOf(operand2.charAt(0)));
		int sn=Integer.parseInt(String.valueOf(result.charAt(0)));
		String overflow=String.valueOf((xn&yn&(1-sn))|((1-xn)&(1-yn)&sn));
		result=result+overflow;
		return result;
	}
	public String AdditionF(String operand1, String operand2, int sLength, int eLength, int gLength){
		String result=null;
		String significant=null;
		String overflowBit="0";
		String sign="0";
		String exponent="";
		int exp=0;
		//=========获取符号=============
		char sign1=operand1.charAt(0);
		char sign2=operand2.charAt(0);
		//=========获得指数尾数=========
		String exponent1=operand1.substring(1,eLength+1);
		int[] length={sLength,eLength};
		int expBias1=Integer.parseInt(TrueValue("0"+exponent1, Type.INTEGER, length));
		int exp1=0;
		String exponent2=operand2.substring(1,eLength+1);
		int expBias2=Integer.parseInt(TrueValue("0"+exponent2, Type.INTEGER,length));
		int exp2=0;
		String significant1=operand1.substring(eLength+1);
		String significant2=operand2.substring(eLength+1);
		//=========增加保护位===========
		String guard="";
		for (int i = 0; i < gLength; i++) {
			guard=guard+"0";
		}
		significant1=significant1+guard;
		significant2=significant2+guard;
		//=========判断是否为0==========
		boolean isO1Zero=true;
		boolean isO2Zero=true;
		for (int i = 1; i < operand1.length(); i++) {
			if(operand1.charAt(i)=='1'){isO1Zero=false;break;}
		}
		for (int i = 1; i < operand2.length(); i++) {
			if(operand2.charAt(i)=='1'){isO2Zero=false;break;}
		}
		if(isO1Zero){
			result=operand2+"0";
		}else if(isO2Zero){
			result=operand1+"0";
		}else {
			//-----------添加隐藏位-------------
			if(exponent1.contains("1")){
				significant1="1"+significant1;
				exp1=expBias1-((int)(Math.pow(2, eLength-1)-1));
			}else{
				significant1="0"+significant1;
				exp1=expBias1-((int)(Math.pow(2, eLength-1))-2);
				}
			if(exponent2.contains("1")){
				significant2="1"+significant2;
				exp2=expBias2-((int)(Math.pow(2, eLength-1)-1));
			}else{
				significant2="0"+significant2;
				exp2=expBias2-((int)(Math.pow(2, eLength-1))-2);
				}
			//===========对齐尾数===============
			if(exp1>exp2){
				exponent=exponent1;
				exp=exp1;
				String aidSig2="";
				for (int i = 0; i < exp1-exp2; i++) {
					aidSig2=aidSig2+"0";
				}
				significant2=aidSig2+significant2;
				significant2=significant2.substring(0,1+sLength+gLength);//0,1+sLength+gLength+1
			}else if(exp2>exp1){
				exponent=exponent2;
				exp=exp2;
				String aidSig1="";
				for (int i = 0; i < exp2-exp1; i++) {
					aidSig1=aidSig1+"0";
				}
				significant1=aidSig1+significant1;
				significant1=significant1.substring(0,1+sLength+gLength);//0,1+sLength+gLength+1
			}else if(exp1==exp2){
				exponent=exponent1;
				exp=exp1;
			}
			//----------判断对齐后尾数是否为0-------
			if(!significant1.contains("1")){
				result=operand2+"0";
			}else if(!significant2.contains("1")){
				result=operand1+"0";
			}else {
				//----------符号相等做加法---------------
				if(sign1==sign2){
					significant=Addition("0"+significant1, "0"+significant2, '0', ((1+sLength+gLength)/8+1)*8);
					sign=String.valueOf(operand1.charAt(1));
					significant=significant.substring(0,significant.length()-1);
					int overflowLoc=significant.length()-1-(sLength+2+gLength)+1;
					if (significant.charAt(overflowLoc)=='1') {
						significant=significant.substring(overflowLoc+1,overflowLoc+1+sLength);
						exp=exp+1;
						exp=exp+(int)(Math.pow(2, eLength-1))-1;
						if(exp>=255){overflowBit="1";}
						exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
						result=sign+exponent+significant+overflowBit;
					}else if(significant.charAt(overflowLoc+1)=='1'){
						significant=significant.substring(overflowLoc+2,overflowLoc+2+sLength);
						result=sign+exponent+significant+overflowBit;
					}else{
						for (int i = overflowLoc+2; i < significant.length(); i++) {
							if(significant.charAt(i)=='1'){
								exp=exp-(i-overflowLoc-1);
								exp=exp+(int)(Math.pow(2, eLength-1))-1;
								if(exp<=0){overflowBit="1";}
								exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
								significant=significant.substring(i+1,i+1+sLength);
								result=sign+exponent+significant+overflowBit;
								break;
							}
						}
					}
				}else{
					//------------符号不同做减法，"0"排除负数干扰------------
					significant2=Subtraction("0",significant2,((1+sLength+gLength)/8+1)*8);
					significant2=significant2.substring(significant2.length()-1-(sLength+2+gLength)+1,significant2.length()-1);
					significant=Addition("0"+significant1, "0"+significant2, '0', ((1+sLength+gLength)/8+1)*8);
					significant=significant.substring(0,significant.length()-1);
					int overflowLoc=significant.length()-1-(sLength+2+gLength)+1;
					if(significant.charAt(overflowLoc)=='1'){
						sign=String.valueOf(operand1.charAt(0));
						overflowLoc=significant.length()-1-(sLength+2+gLength)+1;
						for (int i = overflowLoc+2; i < significant.length(); i++) {
							if(significant.charAt(i)=='1'){
								exp=exp-(i-overflowLoc-1);
								exp=exp+(int)(Math.pow(2, eLength-1))-1;
								if(exp<=0){overflowBit="1";}
								exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
								significant=significant.substring(i+1,i+1+sLength);
								result=sign+exponent+significant+overflowBit;
								break;
							}
						}
					}else{
						if(operand1.charAt(0)=='1'){
							sign="0";
						}else {sign="1";}
						significant=Subtraction("0", significant, (significant.length()/8+1)*8);
						significant="0"+significant.substring(significant.length()-1-(sLength+2+gLength)+2,significant.length()-1);
						overflowLoc=significant.length()-1-(sLength+2+gLength)+1;
						for (int i = overflowLoc+2; i < significant.length(); i++) {
							if(significant.charAt(i)=='1'){
								exp=exp-(i-overflowLoc-1);
								exp=exp+(int)(Math.pow(2, eLength-1))-1;
								if(exp<=0){overflowBit="1";}
								exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
								significant=significant.substring(i+1,i+1+sLength);
								result=sign+exponent+significant+overflowBit;
								break;
							}
						}
					}
				}
				
				
			}
				
		}
		
		return result;
	}
	public String Subtraction (String operand1, String operand2, int length){
		String absOperand2=this.Negation(operand2);
		String result=null;
		result=this.Addition(operand1, absOperand2, '1', length);
		return result;
	}
	public String SubtractionF(String operand1, String operand2, int sLength, int eLength, int gLength){
		String result=null;
		if(operand2.charAt(0)=='0'){
			operand2="1"+operand2.substring(1);
		}else {
			operand2="0"+operand2.substring(1);
		}
		result=AdditionF(operand1, operand2, sLength, eLength, gLength);
		return result;
	}
	public String Multiplication (String operand1, String operand2, int length){
		String product="";
		String multiplicand=null;
		String multiplier=null;
		//Turn operands into required length:
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
		//Initialize the registers:
		for(int i=0;i<length;i++){
			product=product+"0";
		}

		multiplicand=op1;
		multiplier=op2;
		product=product+multiplier+"0";
		for(int j=0;j<length;j++){
			
			if(product.charAt(length*2)=='1'&&product.charAt(length*2-1)=='0'){
				product=this.Addition(product.substring(0,length),multiplicand, '0', length).substring(0,length)+product.substring(length);
			}
			if(product.charAt(length*2)=='0'&&product.charAt(length*2-1)=='1'){
				product=this.Subtraction(product.substring(0,length), multiplicand, length).substring(0,length)+product.substring(length);
			}
			product=this.RightShift(product, 1).substring(0,length*2+1);

		}
		product=product.substring(0,length*2);
		return product;
	}
	
	public String MultiplicationF(String operand1, String operand2, int sLength, int eLength){
		String result=null;
		String sign=null;
		String exponent=null;
		int exp=0;
		String significant=null;
		//=========判断符号============
		if (operand1.charAt(0)==operand2.charAt(0)) {
			sign="0";
		}else {
			sign="1";
		}
		//=========获得指数尾数=========
		String exponent1=operand1.substring(1,eLength+1);
		int[] length={sLength,eLength};
		int expBias1=Integer.parseInt(TrueValue("0"+exponent1, Type.INTEGER, length));
		int exp1=0;
		String exponent2=operand2.substring(1,eLength+1);
		int expBias2=Integer.parseInt(TrueValue("0"+exponent2, Type.INTEGER,length));
		int exp2=0;
		String significant1=operand1.substring(eLength+1);
		String significant2=operand2.substring(eLength+1);
		//=========判断是否为0==========
		boolean isO1Zero=true;
		boolean isO2Zero=true;
		for (int i = 1; i < operand1.length(); i++) {
			if(operand1.charAt(i)=='1'){isO1Zero=false;break;}
		}
		for (int i = 1; i < operand2.length(); i++) {
			if(operand2.charAt(i)=='1'){isO2Zero=false;break;}
		}
		if(isO1Zero){
			String tempResult="";
			for (int i = 0; i < sLength+eLength; i++) {
				tempResult=tempResult+"0";
			}
			result=sign+tempResult;
		}else if(isO2Zero){
			String tempResult="";
			for (int i = 0; i < sLength+eLength; i++) {
				tempResult=tempResult+"0";
			}
			result=sign+tempResult;
		}else {
			//-----------添加隐藏位-------------
			if(exponent1.contains("1")){
				significant1="1"+significant1;
				exp1=expBias1-((int)(Math.pow(2, eLength-1)-1));
			}else{
				significant1="0"+significant1;
				exp1=expBias1-((int)(Math.pow(2, eLength-1))-2);
				}
			if(exponent2.contains("1")){
				significant2="1"+significant2;
				exp2=expBias2-((int)(Math.pow(2, eLength-1)-1));
			}else{
				significant2="0"+significant2;
				exp2=expBias2-((int)(Math.pow(2, eLength-1))-2);
				}
			exp=exp1+exp2;
			exponent=Complement(String.valueOf(exp=exp+(int)(Math.pow(2, eLength-1))-1), eLength+1).substring(1,eLength+1);
			
			String inf;
			String tempExponent="";
			for (int i = 0; i < eLength; i++) {
				tempExponent=tempExponent+"1";
			}
			String tempSignificant="";
			for (int i = 0; i < sLength; i++) {
				tempSignificant=tempSignificant+"0";
			}
			inf=sign+tempExponent+tempSignificant;
			
			if(exp>=255){
				//==========溢出，返回无穷=======
				result=inf;
			}else{
				significant=Multiplication("0"+significant1+"0", "0"+significant2+"0", ((sLength+3)/8+1)*8);
				for(int i = 0; i <= sLength + 1; i++) {
					significant2 = "0" + significant2;
				}
				for(int i = 0; i < sLength + 1; i++) {
					if(significant2.charAt(sLength * 2 + 2) == '1') {
						significant2 = this.Addition(significant2.substring(0, sLength + 2), "0" + significant1, '0', sLength + 2).substring(0, sLength + 2) + significant2.substring(sLength + 2);
					}
					significant2 = this.LogicRightShift(significant2, 1);
				}
				if(significant2.substring(0,3).equals("000")){
					for (int i = 1; i < significant2.length(); i++) {
						if(significant2.charAt(i+2)=='1'){
							exp=exp-i;
							exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
							significant=significant2.substring(i+3,i+3+sLength+1);
							result=sign+exponent+significant;
							break;
						}
					}
				}else{
					for(int i=0;i<3;i++){
						if(significant2.charAt(i)=='0'){
							exp=exp+(2-i);
							exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
							significant=significant2.substring(i+3,i+3+sLength-1)+"0";
							result=sign+exponent+significant;
							break;
						}
					}
				}
			
		/*		significant=significant.substring(0,significant.length());
				
				int overflowLoc=significant.length()-(sLength+1)*2-1-1;
				if (significant.charAt(overflowLoc)=='1') {
					significant=significant.substring(overflowLoc+1,overflowLoc+1+sLength);
					exp=exp+1;
					exp=exp+(int)(Math.pow(2, eLength-1))-1;
					if(exp>=255){
						result=inf;
						}else{
							exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
							result=sign+exponent+significant;
						}
				}else if(significant.charAt(overflowLoc+1)=='1'){
					significant=significant.substring(overflowLoc+2,overflowLoc+2+sLength);
					result=sign+exponent+significant;
				}else{
					for (int i = overflowLoc+2; i < significant.length(); i++) {
						if(significant.charAt(i)=='1'){
							exp=exp-(i-overflowLoc-1);
							exp=exp+(int)(Math.pow(2, eLength-1))-1;
							if(exp<=0){
								result=inf;
							}else{
								exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
								significant=significant.substring(i+1,i+1+sLength);
								result=sign+exponent+significant;
								}
							break;
						}
					}
				}*/
				
				}
			
		}	
		
		return result;
	}
	
	public String Division (String operand1, String operand2, int length){

		String result="";
		String quotient;
		String remainder;
		
		//Turn operands into required length:
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
		String dividend=op1;
		String divisor=op2;
		
		//Initialize the registers:
		for(int i=0;i<length;i++){
			result=result+dividend.charAt(0);
		}
		result=result+dividend;
		char flag=result.charAt(0);
		for(int i=0;i<length;i++){
			if(flag==divisor.charAt(0)){
				result=this.Subtraction(result.substring(0,length), divisor, length).substring(0,length)+result.substring(length);
			}else{
				result=this.Addition(result.substring(0,length), divisor,'0', length).substring(0,length)+result.substring(length);
			}
			flag=result.charAt(0);
			if(flag==divisor.charAt(0)){
				result=result+"1";
			}else{
				result=result+"0";
			}
			result=result.substring(1);
		}
		if(flag==divisor.charAt(0)){
			result=this.Subtraction(result.substring(0,length), divisor, length).substring(0,length)+result.substring(length);
		}else{
			result=this.Addition(result.substring(0,length), divisor,'0', length).substring(0,length)+result.substring(length);
		}
		flag=result.charAt(0);
		if(flag==divisor.charAt(0)){
			result=result+"1";
		}else{
			result=result+"0";
		}
		
		quotient=result.substring(length+1);
		remainder=result.substring(0,length);
		if(dividend.charAt(0)!=divisor.charAt(0)&&quotient.charAt(0)=='1'){
			quotient=this.Addition(quotient, "0001", '0', length).substring(0,length);
		}
		if(remainder.charAt(0)!=dividend.charAt(0)){
			if(dividend.charAt(0)==divisor.charAt(0)){
				remainder=this.Addition(remainder, divisor, '0', length).substring(0,length);
			}else{
				remainder=this.Subtraction(remainder, divisor, length).substring(0,length);
			}
		}
		return quotient+remainder;
	}
	public String DivisionF(String operand1, String operand2, int sLength, int eLength){
		String result=null;
		String sign=null;
		String exponent=null;
		int exp=0;
		String significant=null;
		//=========判断符号============
		if (operand1.charAt(0)==operand2.charAt(0)) {
			sign="0";
		}else {
			sign="1";
		}
		//=========获得指数尾数=========
		String exponent1=operand1.substring(1,eLength+1);
		int[] length={sLength,eLength};
		int expBias1=Integer.parseInt(TrueValue("0"+exponent1, Type.INTEGER, length));
		int exp1=0;
		String exponent2=operand2.substring(1,eLength+1);
		int expBias2=Integer.parseInt(TrueValue("0"+exponent2, Type.INTEGER,length));
		int exp2=0;
		String significant1=operand1.substring(eLength+1);
		String significant2=operand2.substring(eLength+1);
		//=========判断是否为0==========
		boolean isO1Zero=true;
		boolean isO2Zero=true;
		for (int i = 1; i < operand1.length(); i++) {
			if(operand1.charAt(i)=='1'){isO1Zero=false;break;}
		}
		for (int i = 1; i < operand2.length(); i++) {
			if(operand2.charAt(i)=='1'){isO2Zero=false;break;}
		}
		if(isO1Zero){
			String tempResult="";
			for (int i = 0; i < sLength+eLength; i++) {
				tempResult=tempResult+"0";
			}
			result=sign+tempResult;
		}else if(isO2Zero){
			String tempResult="";
			for (int i = 0; i < eLength; i++) {
				tempResult=tempResult+"1";
			}
			for(int i=0;i<sLength;i++){
				tempResult=tempResult+"0";
			}
			result=sign+tempResult;
		}else{
			//-----------添加隐藏位-------------
			if(exponent1.contains("1")){
				significant1="1"+significant1;
				exp1=expBias1-((int)(Math.pow(2, eLength-1)-1));
			}else{
				significant1="0"+significant1;
				exp1=expBias1-((int)(Math.pow(2, eLength-1))-2);
				}
			if(exponent2.contains("1")){
				significant2="1"+significant2;
				exp2=expBias2-((int)(Math.pow(2, eLength-1)-1));
			}else{
				significant2="0"+significant2;
				exp2=expBias2-((int)(Math.pow(2, eLength-1))-2);
				}
			exp=exp1-exp2;
			exponent=Complement(String.valueOf(exp=exp+(int)(Math.pow(2, eLength-1))-1), eLength+1).substring(1,eLength+1);
			//============不恢复余数除法==========
			for(int i = 0; i < sLength + 1; i++) {
				significant1 = significant1 + "0";
			}
			for(int i = 0; i < sLength + 1; i++) {
				significant1 = this.Subtraction("0" + significant1.substring(0, sLength + 1), "0" + significant2, sLength + 2).substring(0, sLength + 2) + significant1.substring(sLength + 1);
				if(significant1.charAt(0) == '1') {
					significant1 = this.Addition(significant1.substring(0, sLength + 2), "0" + significant2, '0', sLength + 2).substring(0, sLength + 2) + significant1.substring(sLength + 2);
					significant1 = significant1 + "0";
				}else {
					significant1 = significant1 + "1";
				}
				significant1 = this.LeftShift(significant1, 1).substring(1, sLength * 2 + 3);
			}
			significant1 = significant1.substring(sLength + 1);
			significant=significant1.substring(0,sLength);
			if(significant.charAt(0)=='1'){
				result=sign+exponent+significant;
			}else{
			
			for (int i = 1; i < significant.length(); i++) {
				if(significant.charAt(i)=='1'){
					exp=exp-i;
					exponent=Complement(String.valueOf(exp), eLength+1).substring(1,eLength+1);
					significant=this.LeftShift(significant, i);
					result=sign+exponent+significant;
						}
					break;
				}
			
			}
		}

		
		
		return result;
		
	}
	public String Calculation (String number1, String number2,Type type, Operation operation, int[] length){
		String tempResult=null;
		String result=null;
		String op1=null;
		String op2=null;
		switch(type){
		case INTEGER:
			int lengthI=length[0];
			op1=this.Complement(number1, lengthI);
			op2=this.Complement(number2, lengthI);
			int[] lengthNull=null;
			switch(operation){
			case ADDITION:
				tempResult=this.Addition(op1, op2, '0', lengthI).substring(0,lengthI);
				result=this.TrueValue(tempResult,Type.INTEGER,lengthNull);
				break;
			case SUBTRACTION:
				tempResult=this.Subtraction(op1, op2, lengthI).substring(0, lengthI);
				result=this.TrueValue(tempResult,Type.INTEGER,lengthNull);
				break;
			case MULTIPLICATION:
				tempResult=this.Multiplication(op1, op2, lengthI);
				result=this.TrueValue(tempResult,Type.INTEGER,lengthNull);
				break;
			case DIVISION:
				tempResult=this.Division(op1, op2, lengthI).substring(0,lengthI);
				result=this.TrueValue(tempResult,Type.INTEGER,lengthNull);
				break;
			default:
				System.out.println("Wrong Operation");
				break;
			}
			break;
		case FLOAT:
			int sLength=length[0];
			int eLength=length[1];
			int gLength=length[2];
			op1=this.FloatRepresentation(number1, sLength, eLength);
			op2=this.FloatRepresentation(number2, sLength, eLength);
			int[] lengthF={sLength,eLength};
			switch (operation) {
			case ADDITION:
				tempResult=this.AdditionF(op1, op2, sLength, eLength, gLength).substring(0,1+sLength+eLength+1);
				result=this.TrueValue(tempResult, Type.FLOAT, lengthF);
				break;
			case SUBTRACTION:
				tempResult=this.SubtractionF(op1, op2, sLength, eLength, gLength).substring(0,1+sLength+eLength+1);
				result=this.TrueValue(tempResult, Type.FLOAT, lengthF);
				break;
			case MULTIPLICATION:
				tempResult=this.MultiplicationF(op1, op2, sLength, eLength);
				result=this.TrueValue(tempResult, Type.FLOAT, lengthF);
				break;
			case DIVISION:
				tempResult=this.DivisionF(op1, op2, sLength, eLength);
				result=this.TrueValue(tempResult, Type.FLOAT, lengthF);
			default:
				break;
			}
		default:
			break;
		}
		return result;
	}

}
