package si.fri.prpo.seminarska.dtos;

public class ArtikelDto {

    private String naziv;
    private String opis;

    public ArtikelDto() {
    }

    public ArtikelDto(String naziv) {
        this.naziv = naziv;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }
}
