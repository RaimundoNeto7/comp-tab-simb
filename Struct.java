class Struct {
    static final int // categorias de tipos
    Nenhum = 0,
            Int = 1,
            Vetor = 2,
            Char = 3;
    int cat;
    Struct tipoElemento;

    public Struct(int cat) {
        this.cat = cat;
    }

    public Struct(int cat, Struct tipoElemento) {
        this.cat = cat;
        this.tipoElemento = tipoElemento;
    }

    // verifica se tipo eh referencia
    public boolean tipoPassadoReferencia() {
        return cat == Vetor;
    }

    // checa se "this" é assinalável para "dest"
    public boolean assinalavel(Struct dest) {
        return dest == this ||
                (this.cat == Vetor &&
                        dest.cat == Vetor &&
                        dest.tipoElemento == this.tipoElemento);
    }

}