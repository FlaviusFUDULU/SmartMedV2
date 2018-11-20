package Tratamente;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by ffudulu on 09-Jun-17.
 */

public class Internare {
    private String dataInternarii;
    private Vector<String> urlAnalize;
    private ArrayList<Tratament> tratamente;
    private String dataExternarii;

    public Internare(String dataInternarii, Vector<String> urlAnalize, ArrayList<Tratament> tratamente, String dataExternarii) {
        this.dataInternarii = dataInternarii;
        this.urlAnalize = urlAnalize;
        this.tratamente = tratamente;
        this.dataExternarii = dataExternarii;
    }

    public Internare(String dataInternarii, Vector<String> urlAnalize, ArrayList<Tratament> tratamente) {
        this.dataInternarii = dataInternarii;
        this.urlAnalize = urlAnalize;
        this.tratamente = tratamente;
    }

    public Internare(String dataInternarii, ArrayList<Tratament> tratamente) {
        this.dataInternarii = dataInternarii;
        this.tratamente = tratamente;
    }

    public Internare(String dataInternarii, ArrayList<Tratament> tratamente, String dataExternarii) {
        this.dataInternarii = dataInternarii;
        this.tratamente = tratamente;
        this.dataExternarii = dataExternarii;
    }

    public Internare() {
    }

    public String getDataInternarii() {
        return dataInternarii;
    }

    public Vector<String> getUrlAnalize() {
        return urlAnalize;
    }

    public ArrayList<Tratament> getTratamente() {
        return tratamente;
    }

    public String getDataExternarii() {
        return dataExternarii;
    }
}
