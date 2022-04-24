import java.lang.*;

public class TabSym {
    static Escopo escopoAtual;
    static int nivelAtual;
    static Struct tipoInt;
    static Struct semTipo;
    static Obj objTamVetor;
    static Obj semObj;
    Parser parser;

    public TabSym(Parser parser) {
        this.parser = parser;
        iniciar();
    }

    Obj inserir(int cat, String nome, Struct tipo) {
        Obj obj = new Obj(cat, nome, tipo);
        if (cat == Obj.Var) {
            obj.end = escopoAtual.nVars;
            escopoAtual.nVars++;
            obj.nivel = nivelAtual;
        }
        Obj p = escopoAtual.locais, ult = null;
        while (p != null) {
            if (p.nome.equals(nome))
                parser.erro("\"" + nome + "\" já foi declarado!");
            ult = p;
            p = p.prox;
        }
        if (ult == null)
            escopoAtual.locais = obj;
        else
            ult.prox = obj;
        return obj;
    }

    Obj buscar(String nome) {
        for (Escopo s = escopoAtual; s != null; s = s.acima)
            for (Obj p = s.locais; p != null; p = p.prox)
                if (p.nome.equals(nome))
                    return p;
        parser.erro(nome + " não foi declarado!");
        return semObj;
    }

    void abrirEscopo(String nome) {
        Escopo s = new Escopo(nome, escopoAtual);
        escopoAtual = s;
        nivelAtual++;
    }

    void fecharEscopo() {
        escopoAtual = escopoAtual.acima;
        nivelAtual--;
    }

    public void iniciar() {
        Obj o;
        escopoAtual = new Escopo("Universo", null);
        nivelAtual = -1;
        semObj = new Obj(Obj.Var, "null", semTipo);

        tipoInt = new Struct(Struct.Int);
        semTipo = new Struct(Struct.Nenhum);

        inserir(Obj.Tipo, "int", tipoInt);
        inserir(Obj.Tipo, "void", semTipo);
    }

    // dump Structs
    public void dumpStruct(Struct tipo) {
        String cat;
        switch (tipo.cat) {
            case Struct.Int:
                cat = "Tipo: Int";
                break;
            case Struct.Vetor:
                cat = "Tipo: Vetor";
                break;
            default:
                cat = "Tipo: Void";
        }
        System.out.print(cat);
        if (tipo.cat == Struct.Vetor) {
            // System.out.print("[" + tipo.nElementos + "] = (");
            System.out.print("[] = (");
            dumpStruct(tipo.tipoElemento);
            System.out.print(")");
        }
    }

    // dump Objs
    public void dumpObj(Obj o, int n) {
        for (int i = 0; i < n; i++)
            System.out.print("  ");
        switch (o.cat) {
            case Obj.Const:
                System.out.print("Const " + o.nome + " = " + o.val + " (");
                break;
            case Obj.Var:
                System.out.print("Var " + o.nome + " end: " + o.end + " nivel=" + o.nivel + " (");
                break;
            case Obj.Tipo:
                System.out.print("Tipo " + o.nome + " (");
                break;
            case Obj.Meth:
                System.out.print("Metodo \"" + o.nome + "\" Endereco: " + o.end + " #Param: " + o.nPars + " -> (");
                break;
            default:
                System.out.print("Null " + o.nome + " (");
        }
        dumpStruct(o.tipo);
        System.out.println(")");
    }

    // dump Escopos
    public void dumpEscopo(Obj head, int n) {
        for (Obj o = head; o != null; o = o.prox) {
            dumpObj(o, n);
            if (o.cat == Obj.Meth || o.cat == Obj.Prog)
                dumpEscopo(o.locais, n + 1);
        }
    }

    // dump de escopo atual
    public void dump() {
        System.out.println("====");
        for (Escopo e = escopoAtual; e != null; e = e.acima) {
            System.out.println(e.nome);
            System.out.println("----");
            dumpEscopo(e.locais, 0);
            System.out.println("----");
        }
        System.out.println("====");
    }

}