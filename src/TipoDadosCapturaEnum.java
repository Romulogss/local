import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum TipoDadosCapturaEnum {
    FOTO_CLIENTE("Foto do cliente", "CLI"),
    DOCUMENTO_IDENTIFICACAO("Documento de identificação", "ID"),
    DOCUMENTO_IDENTIFICACAO_VERSO("Verso documento de identificação", "ID_VERSO"),
    CPF("CPF", "CPF"),
    COMPROVANTE_RESIDENCIA("Comprovante de residência", "CR"),
    EMPREENDIMENTO_FACHADA("Fachada do empreendimento", "EMP_FA"),
    EMPREENDIMENTO_INTERNO("Interno do empreendimento", "EMP_IN"),
    PROCURACAO("Procuração", "PROC"),
    CERTIDAO_CASAMENTO("Certidão de casamento", "CERT_CAS"),
    CERTIDAO_OBITO("Certidão de óbtio", "CERT_OBT"),
    DECLARACAO("Declaração", "DECL"),
    OUTROS("Outros documentos", "OUTROS"),
    ;

    private final String description;
    private final String oldValue;


    TipoDadosCapturaEnum(String description, String oldValue) {
        this.description = description;
        this.oldValue = oldValue;
    }


    public String getValue() {
        return name();
    }


    public String getDescription() {
        return description;
    }

    public String getOldValue() {
        return oldValue;
    }

    public static TipoDadosCapturaEnum oldValueOff(String oldValud) {
        return Arrays.stream(TipoDadosCapturaEnum.values())
                .filter(tipo -> tipo.getOldValue().equalsIgnoreCase(oldValud))
                .findFirst()
                .orElse(null);
    }

    public static List<String> getNamesForQuery() {
        List<String> result = new ArrayList<>();
        for (TipoDadosCapturaEnum tipo : TipoDadosCapturaEnum.values()) {
            result.add(tipo.name());
        }
        return result;
    }
}
