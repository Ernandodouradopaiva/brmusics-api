package br.gov.ce.sps.projetoa.domain.model.enums;

public enum EnumUnidade {

	/*
	128	Sobral
	129	Antônio Bezerra - Fortaleza
	130	Messejana - Fortaleza
	131	Parangaba - Fortaleza
	132	Centro - Fortaleza
	133	Papicu - Fortaleza
	134	Juazeiro do Norte
	 */
	
	SOBRAL(128, "CALCULO_%s_SOBRAL", "%s_SOBRAL"),
	ANTONIO_BEZERRA(129, "CALCULO_%s_ANTONIO", "%s_ANTONIO"),
	MESSEJANA(130, "CALCULO_%s_MESSEJANA", "%s_MESSEJANA"),
	PARANGABA(131, "CALCULO_%s_PARANGABA", "%s_PARANGABA"),
	CENTRO(132, "CALCULO_%s_CENTRO", "%s_CENTRO"),
	PAPICU(133, "CALCULO_%s_PAPICU", "%s_PAPICU"),
	JUAZEIRO(134, "CALCULO_%s_JUAZEIRO", "%s_JUAZEIRO"); 
		

	private final long id;
	private final String placeholderCalculo;
	private final String placeholderTotal;

	public long getId() {
		return id;
	}

	public String getPlaceholderCalculo(String tipo) {
		return String.format(placeholderCalculo, tipo);
	}

	public String getPlaceholderTotal(String tipo) {
		return String.format(placeholderTotal, tipo);
	}

	EnumUnidade(long id, String placeHolderCalculo, String placeholderTotal) {
		this.id = id;
		this.placeholderCalculo = placeHolderCalculo;
		this.placeholderTotal = placeholderTotal;
	}

	public static EnumUnidade getFromId(long id) {
		for (EnumUnidade o : EnumUnidade.values()) {
			if (o.getId() == id) {
				return o;
			}
		}
		return null;
	}

}
