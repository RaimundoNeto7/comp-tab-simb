class Escopo {
    String nome;
    Escopo acima;
    Obj locais;
    int nVars;

    public Escopo(String nome, Escopo acima) {
        this.nome = nome;
        this.acima = acima;
    }
}