package br.edu.unibh.doesaude.util;

public final class CnpjUtils {

    private static final int[] PESOS_DV1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    private static final int[] PESOS_DV2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

    private CnpjUtils() {
    }

    // tira a máscara e confere os dígitos verificadores; devolve só os 14 dígitos, ou null se inválido
    public static String normalizarSeValido(String cnpj) {
        if (cnpj == null) return null;
        String digitos = cnpj.replaceAll("\\D", "");
        return valido(digitos) ? digitos : null;
    }

    private static boolean valido(String cnpj) {
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) return false;
        int[] d = cnpj.chars().map(c -> c - '0').toArray();
        return d[12] == digitoVerificador(d, PESOS_DV1) && d[13] == digitoVerificador(d, PESOS_DV2);
    }

    private static int digitoVerificador(int[] digitos, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) soma += digitos[i] * pesos[i];
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
