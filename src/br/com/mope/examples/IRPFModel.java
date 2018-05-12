package br.com.mope.examples;

import br.com.mope.Converter;
import mobi.core.Mobi;
import mobi.core.common.Relation;
import mobi.core.concept.Attribute;
import mobi.core.concept.AttributeTypeEnum;
import mobi.core.concept.AttributeValue;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;

public class IRPFModel {
	
	public static void main(String[] args) {
		Converter converter = new Converter(IRPFModel.createScenary());
		
		try {
			converter.exportCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Mobi createScenary() {
		final Mobi mobi = new Mobi("CalcIR");

		final Instance iUedson = new Instance("Uedson");
		final Instance iEduardo = new Instance("Eduardo");
		final Instance iTabela2017 = new Instance("Tabela2017");
		final Instance iEducacao = new Instance("Educacao");
		final Instance iSaude = new Instance("Saude");
		final Instance iPGBL = new Instance("PGBL");

		final Class cPagador  = new Class("Pagador");
		final Class cDeducao  = new Class("Deducao");
		final Class cTabela = new Class("Tabela");

		final Attribute aNome = new Attribute("nome", cPagador, AttributeTypeEnum.STRING);
		final Attribute aCpf = new Attribute("cpf", cPagador, AttributeTypeEnum.STRING);
		final Attribute aGanhoAnual = new Attribute("ganhoAnual", cPagador, AttributeTypeEnum.DOUBLE);
		final Attribute aTotalAPagar = new Attribute("totalAPagar", cPagador, AttributeTypeEnum.DOUBLE);
		
//		final Attribute aValorFaixaIsento = new Attribute("valorFaixaIsento", cPagador, AttributeTypeEnum.DOUBLE);
		final Attribute aValorFaixa075 = new Attribute("valorFaixa075", cPagador, AttributeTypeEnum.DOUBLE);
		final Attribute aValorFaixa150 = new Attribute("valorFaixa150", cPagador, AttributeTypeEnum.DOUBLE);
		final Attribute aValorFaixa225 = new Attribute("valorFaixa225", cPagador, AttributeTypeEnum.DOUBLE);
		final Attribute aValorFaixa275 = new Attribute("valorFaixa275", cPagador, AttributeTypeEnum.DOUBLE);
		
		final Attribute aValor = new Attribute("valor", cDeducao, AttributeTypeEnum.DOUBLE);
		final Attribute aTeto = new Attribute("teto", cDeducao, AttributeTypeEnum.DOUBLE);
		
		final Attribute aAnoExercio = new Attribute("anoExercicio", cTabela, AttributeTypeEnum.INTEGER);
		final Attribute aFaixaIsento = new Attribute("faixaIsento", cTabela, AttributeTypeEnum.DOUBLE);
		final Attribute aFaixa075 = new Attribute("faixa075", cTabela, AttributeTypeEnum.DOUBLE);
		final Attribute aFaixa150 = new Attribute("faixa150", cTabela, AttributeTypeEnum.DOUBLE);
		final Attribute aFaixa225 = new Attribute("faixa225", cTabela, AttributeTypeEnum.DOUBLE);

		try {
			mobi.addConcept(cPagador);
			mobi.addConcept(cDeducao);
			mobi.addConcept(cTabela);
			
			mobi.isOneOf(iUedson, cPagador);
			mobi.isOneOf(iEduardo, cPagador);
			mobi.isOneOf(iTabela2017, cTabela);
			mobi.isOneOf(iEducacao, cDeducao);
			mobi.isOneOf(iSaude, cDeducao);
			mobi.isOneOf(iPGBL, cDeducao);
			
			mobi.belongsTo(cPagador, aNome, false);
			mobi.belongsTo(cPagador, aCpf, false);
			mobi.belongsTo(cPagador, aGanhoAnual, false);
			mobi.belongsTo(cPagador, aTotalAPagar, false);
//			mobi.belongsTo(cPagador, aValorFaixaIsento, false);
			mobi.belongsTo(cPagador, aValorFaixa075, false);
			mobi.belongsTo(cPagador, aValorFaixa150, false);
			mobi.belongsTo(cPagador, aValorFaixa225, false);
			mobi.belongsTo(cPagador, aValorFaixa275, false);
			mobi.belongsTo(cDeducao, aValor, false);
			mobi.belongsTo(cDeducao, aTeto, false);
			
			mobi.belongsTo(cTabela, aAnoExercio, false);
			mobi.belongsTo(cTabela, aFaixaIsento, false);
			mobi.belongsTo(cTabela, aFaixa075, false);
			mobi.belongsTo(cTabela, aFaixa150, false);
			mobi.belongsTo(cTabela, aFaixa225, false);
			
			iTabela2017.getAttributeValueHashMap().put(aAnoExercio.getUri(), new AttributeValue(aAnoExercio.getType(), "2017"));
			iTabela2017.getAttributeValueHashMap().put(aFaixaIsento.getUri(), new AttributeValue(aFaixaIsento.getType(), "22847.76"));
			iTabela2017.getAttributeValueHashMap().put(aFaixa075.getUri(), new AttributeValue(aFaixa075.getType(), "11072.04"));
			iTabela2017.getAttributeValueHashMap().put(aFaixa150.getUri(), new AttributeValue(aFaixa150.getType(), "11092.80"));
			iTabela2017.getAttributeValueHashMap().put(aFaixa225.getUri(), new AttributeValue(aFaixa225.getType(), "10963.56"));
			
			mobi.setAttributeValue(iUedson, aNome, "Uedson Reis");
			mobi.setAttributeValue(iUedson, aCpf, "888.777.666-55");
			mobi.setAttributeValue(iUedson, aGanhoAnual, "50000.00");
			
			mobi.setAttributeValue(iEduardo, aNome, "Eduardo Jorge");
			mobi.setAttributeValue(iEduardo, aCpf, "777.666.555-44");
			mobi.setAttributeValue(iEduardo, aGanhoAnual, "150000.00");
			
			mobi.setAttributeValue(iEducacao, aValor, "13000.00");
			mobi.setAttributeValue(iEducacao, aTeto, "3561.50");
			
			mobi.setAttributeValue(iSaude, aValor, "1000.00");
			mobi.setAttributeValue(iSaude, aTeto, null);
			
			mobi.setAttributeValue(iPGBL, aValor, "20000.00");
			mobi.setAttributeValue(iPGBL, aTeto, "18000.00");
			
			Relation rRecebeuDe = mobi.createUnidirecionalCompositionRelationship("recebeuDe");
			
			rRecebeuDe.setClassA(cTabela);
			rRecebeuDe.setClassB(cPagador);
			rRecebeuDe.addInstanceRelation(iTabela2017, iUedson);
			rRecebeuDe.addInstanceRelation(iTabela2017, iEduardo);
			rRecebeuDe.processCardinality();
			mobi.addConcept(rRecebeuDe);

			Relation rPagou = mobi.createUnidirecionalCompositionRelationship("pagou");
			
			rPagou.setClassA(cPagador);
			rPagou.setClassB(cDeducao);
			rPagou.addInstanceRelation(iUedson, iEducacao);
			rPagou.addInstanceRelation(iUedson, iSaude);
			rPagou.addInstanceRelation(iEduardo, iSaude);
			rPagou.addInstanceRelation(iEduardo, iPGBL);
			rPagou.processCardinality();
			mobi.addConcept(rPagou);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mobi;
	}

}