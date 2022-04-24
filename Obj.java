class Obj {
    static final int Const = 0,
            Var = 1,
            Tipo = 2,
            Meth = 3,
            Prog = 4;
    int cat;
    String nome;
    Struct tipo;
    Obj prox;
    int val; // valor pra Const
    char valChar; // valor pra Const do tipo Char
    int end; // Endereco pra Var e Meth
    int nivel; // Var: 0 = global, 1 = local
    int nPars; // Meth: nro de parametros
    Obj locais; // Meth: parametros locais
    Obj ultObj; // Meth: ultimo Obj em locais

    public Obj(int cat, String nome, Struct tipo) {
        this.cat = cat;
        this.nome = nome;
        this.tipo = tipo;
        this.locais = null;
        this.prox = null;
        this.ultObj = null;
        nPars = 0;
    }

    public void setTipo(Struct tipo) {
        this.tipo = tipo;
    }

    public void adicionaALocais(Obj o) {
        if (locais == null)
            locais = o;
        else
            ultObj.prox = o;
        ultObj = o;
        nPars++;
    }
}
