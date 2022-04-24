

public class Parser {
	public static final int _EOF = 0;
	public static final int _number = 1;
	public static final int _ident = 2;
	public static final int _charConst = 3;
	public static final int maxT = 35;
	public static final int _option = 36;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	private TabSym ts;
    private Obj objMethAtual;

    public void erro(String msg) {
        errors.SemErr(t.line, t.col, msg);
    }



	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			if (la.kind == 36) {
				ts.dump(); 
			}
			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void uJts() {
		Expect(4);
		Expect(2);
		objMethAtual = null;
		ts = new TabSym(this);
		ts.abrirEscopo("Global");
		
		while (la.kind == 2 || la.kind == 9 || la.kind == 13) {
			if (la.kind == 9) {
				ConstDecl();
			} else if (la.kind == 2) {
				VarDecl();
			} else {
				ClassDecl();
			}
		}
		Expect(5);
		while (la.kind == 2) {
			MethodDecl();
		}
		Expect(6);
		ts.fecharEscopo(); 
	}

	void ConstDecl() {
		Obj o; objVar objRet; 
		Expect(9);
		objRet = Type();
		o = ts.buscar(objRet.nome); 
		Expect(2);
		o = ts.inserir(Obj.Const, t.val, o.tipo); 
		Expect(10);
		Expect(1);
		o.val = Integer.parseInt(t.val); 
		Expect(11);
	}

	void VarDecl() {
		Obj o; objVar objRet; 
		objRet = Type();
		o = ts.buscar(objRet.nome); 
		Expect(2);
		o = ts.inserir(Obj.Var, t.val, o.tipo); 
		while (la.kind == 12) {
			Get();
			Expect(2);
			o = ts.inserir(Obj.Var, t.val, o.tipo); 
		}
		Expect(11);
	}

	void ClassDecl() {
		Expect(13);
		Expect(2);
		Expect(5);
		while (la.kind == 2) {
			VarDecl();
		}
		Expect(6);
	}

	void MethodDecl() {
		objVar objRet; 
		objRet = Type();
		Obj o = ts.buscar(objRet.nome); 
		Expect(2);
		Struct tipo = ts.buscar(objRet.nome).tipo;
		objMethAtual = ts.inserir(Obj.Meth, t.val, tipo);
		ts.abrirEscopo("Meth " + t.val); 
		Expect(14);
		if (la.kind == 2) {
			FormPars();
		}
		Expect(15);
		while (la.kind == 2) {
			VarDecl();
		}
		Block();
		ts.fecharEscopo(); 
	}

	objVar  Type() {
		objVar  retorno;
		Expect(2);
		retorno = new objVar(t.val, false); 
		if (la.kind == 7) {
			Get();
			Expect(8);
			retorno.vetor = true; 
		}
		return retorno;
	}

	void FormPars() {
		objVar objRet; 
		objRet = Type();
		Obj o = ts.buscar(objRet.nome);  
		Expect(2);
		ts.inserir(Obj.Var, t.val, o.tipo); 
		objMethAtual.nPars++;
		
		while (la.kind == 12) {
			Get();
			objRet = Type();
			o = ts.buscar(objRet.nome); 
			Expect(2);
			ts.inserir(Obj.Var, t.val, o.tipo);
			objMethAtual.nPars++;
			
		}
	}

	void Block() {
		Expect(5);
		while (StartOf(1)) {
			Statement();
		}
		Expect(6);
	}

	void Statement() {
		Operand operand = null; 
		switch (la.kind) {
		case 2: {
			operand = Designator();
			if (la.kind == 10) {
				Get();
				operand = Expr();
			} else if (la.kind == 14) {
				ActPars();
			} else SynErr(36);
			Expect(11);
			break;
		}
		case 16: {
			Get();
			Expect(14);
			Condition();
			Expect(15);
			Statement();
			if (la.kind == 17) {
				Get();
				Statement();
			}
			break;
		}
		case 18: {
			Get();
			Expect(14);
			Condition();
			Expect(15);
			Statement();
			break;
		}
		case 19: {
			Get();
			if (StartOf(2)) {
				operand = Expr();
			}
			Expect(11);
			break;
		}
		case 20: {
			Get();
			Expect(14);
			operand = Designator();
			Expect(15);
			Expect(11);
			break;
		}
		case 21: {
			Get();
			Expect(14);
			operand = Expr();
			if (la.kind == 12) {
				Get();
				Expect(1);
			}
			Expect(15);
			Expect(11);
			break;
		}
		case 5: {
			Block();
			break;
		}
		case 11: {
			Get();
			break;
		}
		default: SynErr(37); break;
		}
	}

	Operand  Designator() {
		Operand  operand;
		Operand op2; 
		Expect(2);
		operand = new Operand(ts.buscar(t.val)); 
		while (la.kind == 7 || la.kind == 30) {
			if (la.kind == 30) {
				Get();
				Expect(2);
				operand = new Operand(ts.buscar(t.val)); 
			} else {
				Get();
				op2 = Expr();
				if (op2.tipo != ts.tipoInt)
				 erro("Tipo da expressao deve ser inteiro");
				
				Expect(8);
			}
		}
		return operand;
	}

	Operand  Expr() {
		Operand  operand;
		if (la.kind == 28) {
			Get();
		}
		operand = Term();
		if (operand.tipo != ts.tipoInt)
		 erro("operando deve ser do tipo int");
		
		while (la.kind == 28 || la.kind == 31) {
			Addop();
			operand = Term();
			if (operand.tipo != ts.tipoInt)
			  erro("operando deve ser do tipo int");
			
		}
		return operand;
	}

	void ActPars() {
		Operand operand = null; 
		Expect(14);
		if (StartOf(2)) {
			operand = Expr();
			while (la.kind == 12) {
				Get();
				operand = Expr();
			}
		}
		Expect(15);
	}

	void Condition() {
		Operand operand = null; 
		operand = Expr();
		Relop();
		operand = Expr();
	}

	void Relop() {
		switch (la.kind) {
		case 22: {
			Get();
			break;
		}
		case 23: {
			Get();
			break;
		}
		case 24: {
			Get();
			break;
		}
		case 25: {
			Get();
			break;
		}
		case 26: {
			Get();
			break;
		}
		case 27: {
			Get();
			break;
		}
		default: SynErr(38); break;
		}
	}

	Operand  Term() {
		Operand  operand;
		operand = null; 
		operand = Factor();
		while (la.kind == 32 || la.kind == 33 || la.kind == 34) {
			Mulop();
			operand = Factor();
		}
		return operand;
	}

	void Addop() {
		if (la.kind == 31) {
			Get();
		} else if (la.kind == 28) {
			Get();
		} else SynErr(39);
	}

	Operand  Factor() {
		Operand  operand;
		operand = null; 
		if (la.kind == 2) {
			operand = Designator();
			if (la.kind == 14) {
				ActPars();
			}
		} else if (la.kind == 1) {
			Get();
			operand = new Operand(Integer.parseInt(t.val)); 
		} else if (la.kind == 3) {
			Get();
		} else if (la.kind == 29) {
			Get();
			Expect(2);
			if (la.kind == 7) {
				Get();
				operand = Expr();
				Expect(8);
			}
		} else if (la.kind == 14) {
			Get();
			operand = Expr();
			Expect(15);
		} else SynErr(40);
		return operand;
	}

	void Mulop() {
		if (la.kind == 32) {
			Get();
		} else if (la.kind == 33) {
			Get();
		} else if (la.kind == 34) {
			Get();
		} else SynErr(41);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		uJts();
		Expect(0);

	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x},
		{_x,_x,_T,_x, _x,_T,_x,_x, _x,_x,_x,_T, _x,_x,_x,_x, _T,_x,_T,_T, _T,_T,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x},
		{_x,_T,_T,_T, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _T,_T,_x,_x, _x,_x,_x,_x, _x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "number expected"; break;
			case 2: s = "ident expected"; break;
			case 3: s = "charConst expected"; break;
			case 4: s = "\"program\" expected"; break;
			case 5: s = "\"{\" expected"; break;
			case 6: s = "\"}\" expected"; break;
			case 7: s = "\"[\" expected"; break;
			case 8: s = "\"]\" expected"; break;
			case 9: s = "\"final\" expected"; break;
			case 10: s = "\"=\" expected"; break;
			case 11: s = "\";\" expected"; break;
			case 12: s = "\",\" expected"; break;
			case 13: s = "\"class\" expected"; break;
			case 14: s = "\"(\" expected"; break;
			case 15: s = "\")\" expected"; break;
			case 16: s = "\"if\" expected"; break;
			case 17: s = "\"else\" expected"; break;
			case 18: s = "\"while\" expected"; break;
			case 19: s = "\"return\" expected"; break;
			case 20: s = "\"read\" expected"; break;
			case 21: s = "\"print\" expected"; break;
			case 22: s = "\"==\" expected"; break;
			case 23: s = "\"!=\" expected"; break;
			case 24: s = "\">\" expected"; break;
			case 25: s = "\">=\" expected"; break;
			case 26: s = "\"<\" expected"; break;
			case 27: s = "\"<=\" expected"; break;
			case 28: s = "\"-\" expected"; break;
			case 29: s = "\"new\" expected"; break;
			case 30: s = "\".\" expected"; break;
			case 31: s = "\"+\" expected"; break;
			case 32: s = "\"*\" expected"; break;
			case 33: s = "\"/\" expected"; break;
			case 34: s = "\"%\" expected"; break;
			case 35: s = "??? expected"; break;
			case 36: s = "invalid Statement"; break;
			case 37: s = "invalid Statement"; break;
			case 38: s = "invalid Relop"; break;
			case 39: s = "invalid Addop"; break;
			case 40: s = "invalid Factor"; break;
			case 41: s = "invalid Mulop"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}
