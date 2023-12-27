import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class SegmentoU implements Serializable {

    private final Long lote;
    private final Long numeroRegistro;
    private final String codigoSegmento = "U";
    private final BigDecimal jurosMultasEncargos;
    private final BigDecimal valorDesconto;
    private final BigDecimal valorAbatimento;
    private final BigDecimal valorIOF;
    private final BigDecimal valorPago;
    private final BigDecimal valorLiquidar;
    private final BigDecimal valorOutrasDespesas;
    private final BigDecimal valorOutrosCreditos;
    private final LocalDate dataOcorrencia;
    private final LocalDate dataCredito;
    private final LocalDate dataDebitoTarifa;
    private String nossoNumero;
    private BigDecimal valorTitulo = BigDecimal.ZERO;

    private String bancoRecebimento;

    private Long numeroContrato;

    public SegmentoU(String linha) {
        this.lote = CNABUtils.getNumeroLote(linha);
        this.numeroRegistro = (CNABUtils.getNumeroDeRegistro(linha));
        this.jurosMultasEncargos = (CNABUtils.valorParaBigDecimalDaLinha(linha, 17));
        this.valorDesconto = (CNABUtils.valorParaBigDecimalDaLinha(linha, 32));
        this.valorAbatimento = (CNABUtils.valorParaBigDecimalDaLinha(linha, 47));
        this.valorIOF = (CNABUtils.valorParaBigDecimalDaLinha(linha, 62));
        this.valorPago = (CNABUtils.valorParaBigDecimalDaLinha(linha, 77));
        this.valorLiquidar = (CNABUtils.valorParaBigDecimalDaLinha(linha, 92));
        this.valorOutrasDespesas = (CNABUtils.valorParaBigDecimalDaLinha(linha, 107));
        this.valorOutrosCreditos = (CNABUtils.valorParaBigDecimalDaLinha(linha, 122));
        this.dataOcorrencia = (CNABUtils.valorParaDateDaLinha(linha, 137));
        this.dataCredito = (CNABUtils.valorParaDateDaLinha(linha, 145));
        this.dataDebitoTarifa = (CNABUtils.valorParaDateDaLinha(linha, 157));
    }

    public String getLoteComNumeroRegistro() {
        return lote + "" + numeroRegistro;
    }

    public Long getNumeroRegistro() {
        return numeroRegistro;
    }

    public BigDecimal getJurosMultasEncargos() {
        return jurosMultasEncargos;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public BigDecimal getValorPago() {
        return valorPago;
    }

    public LocalDate getDataOcorrencia() {
        return dataOcorrencia;
    }

    public String getNossoNumero() {
        return nossoNumero;
    }

    public void setNossoNumero(String nossoNumero) {
        this.nossoNumero = nossoNumero;
    }

    public void setValorTitulo(BigDecimal valorTitulo) {
        this.valorTitulo = valorTitulo;
    }

    public Long getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(Long numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public Long getLote() {
        return lote;
    }

    public String getBancoRecebimento() {
        return bancoRecebimento;
    }

    public void setBancoRecebimento(String bancoRecebimento) {
        this.bancoRecebimento = bancoRecebimento;
    }
}
