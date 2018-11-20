package Tratamente;

/**
 * Created by ffudulu on 09-Jun-17.
 */

public class Tratament {
    private String diagnostic;
    private String medicatie;
    private String dataAplicariiTratamentului;
    private String detalii;


    public Tratament(String diagnostic, String medicatie, String dataAplicariiTratamentului,
                                                                                String detalii ) {
        this.diagnostic = diagnostic;
        this.medicatie = medicatie;
        this.dataAplicariiTratamentului = dataAplicariiTratamentului;
        this.detalii = detalii;
    }


    public Tratament(String medicatie, String dataAplicariiTratamentului, String detalii) {

        this.medicatie = medicatie;
        this.dataAplicariiTratamentului = dataAplicariiTratamentului;
        this.detalii = detalii;
    }

    public Tratament(String medicatie, String dataAplicariiTratamentului) {

        this.medicatie = medicatie;
        this.dataAplicariiTratamentului = dataAplicariiTratamentului;
    }

    public Tratament() {
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public String getMedicatie() {
        return medicatie;
    }

    public String getDataAplicariiTratamentului() {
        return dataAplicariiTratamentului;
    }
    public String getDetalii() {
        return detalii;
    }
}
