public class Operand {
    public static final int // item
    Desconhecido = 0,
            Const = 1,
            Local = 2,
            Static = 3,
            Stack = 4,
            Elem = 5,
            Meth = 6;

    public int cat; // Const, Local, Static, Stack, Elem, Meth
    public Struct tipo; // item tipo
    public Obj obj; // Meth
    public int val; // Const: value
    public int end; // Local, Static, Meth: address

    public Operand(Operand other) {
        this.cat = other.cat;
        this.tipo = other.tipo;
        this.obj = other.obj;
        this.val = other.val;
        this.end = other.end;
    }

    public Operand(Obj o) {
        tipo = o.tipo;
        val = o.val;
        end = o.end;
        cat = Stack; // default
        switch (o.cat) {
            case Obj.Const:
                cat = Const;
                break;
            case Obj.Var:
                if (o.nivel == 0)
                    cat = Static;
                else
                    cat = Local;
                break;
            case Obj.Meth:
                cat = Meth;
                obj = o;
                break;
            case Obj.Tipo:
                System.out.println("identificador de tipo nao permitido aqui");
                System.exit(1);
                break;
            default:
                System.out.println("categoria errada de indentificador = " + o.cat);
                System.exit(1);
                break;
        }
    }

    public Operand(int val) {
        cat = Const;
        this.val = val;
        tipo = TabSym.tipoInt;
    }

    public Operand(char val) {
        cat = Const;
        this.val = val;
        tipo = TabSym.tipoChar;
    }

    public Operand(int cat, int val, Struct tipo) {
        this.cat = cat;
        this.val = val;
        this.tipo = tipo;
    }

}