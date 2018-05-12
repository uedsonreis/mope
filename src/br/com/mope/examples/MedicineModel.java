package br.com.mope.examples;

import br.com.mope.Converter;
import mobi.core.Mobi;
import mobi.core.common.Relation;
import mobi.core.concept.Attribute;
import mobi.core.concept.AttributeTypeEnum;
import mobi.core.concept.Class;
import mobi.core.concept.Instance;

public class MedicineModel {
	
	public static void main(String[] args) {
		Converter converter = new Converter(MedicineModel.createScenary());
		
		try {
			converter.exportCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Mobi createScenary() {
		final Mobi mobi = new Mobi("Medicine");

		final Instance iBocaDaMata = new Instance("BocaDaMata");
		final Instance iCajazeirasIV = new Instance("CajazeirasIV");
		final Instance iAdenosina = new Instance("Adenosina");
		final Instance iAcarbose = new Instance("Acarbose");

		final Class cPosto  = new Class("Posto");
		final Class cRemedio  = new Class("Remedio");

		final Attribute aNumero = new Attribute("numero", cPosto, AttributeTypeEnum.INTEGER);
		final Attribute aNome = new Attribute("nome", cPosto, AttributeTypeEnum.STRING);
		final Attribute aLogradouro = new Attribute("logradouro", cPosto, AttributeTypeEnum.STRING);
		final Attribute aBairro = new Attribute("bairro", cPosto, AttributeTypeEnum.STRING);
		final Attribute aCep = new Attribute("cep", cPosto, AttributeTypeEnum.STRING);
		final Attribute aLatitude = new Attribute("latitude", cPosto, AttributeTypeEnum.DOUBLE);
		final Attribute aLongitude = new Attribute("longitude", cPosto, AttributeTypeEnum.DOUBLE);
		
		final Attribute aCodigo = new Attribute("codigo", cRemedio, AttributeTypeEnum.INTEGER);
		final Attribute aPrincipio = new Attribute("principio", cRemedio, AttributeTypeEnum.STRING);
		final Attribute aDosagem = new Attribute("dosagem", cRemedio, AttributeTypeEnum.STRING);
		final Attribute aForma = new Attribute("forma", cRemedio, AttributeTypeEnum.STRING);
		final Attribute aQuantidade = new Attribute("quantidade", cRemedio, AttributeTypeEnum.INTEGER);
		
		try {
			mobi.addConcept(cPosto);
			mobi.addConcept(cRemedio);
			
			mobi.isOneOf(iBocaDaMata, cPosto);
			mobi.isOneOf(iCajazeirasIV, cPosto);
			mobi.isOneOf(iAcarbose, cRemedio);
			mobi.isOneOf(iAdenosina, cRemedio);
			
			mobi.belongsTo(cPosto, aNumero, false);
			mobi.belongsTo(cPosto, aNome, false);
			mobi.belongsTo(cPosto, aLogradouro, false);
			mobi.belongsTo(cPosto, aBairro, false);
			mobi.belongsTo(cPosto, aCep, false);
			mobi.belongsTo(cPosto, aLatitude, false);
			mobi.belongsTo(cPosto, aLongitude, false);
			
			mobi.belongsTo(cRemedio, aCodigo, false);
			mobi.belongsTo(cRemedio, aPrincipio, false);
			mobi.belongsTo(cRemedio, aDosagem, false);
			mobi.belongsTo(cRemedio, aForma, false);
			mobi.belongsTo(cRemedio, aQuantidade, false);
			
			mobi.setAttributeValue(iBocaDaMata, aNumero, "1");
			mobi.setAttributeValue(iBocaDaMata, aNome, "USF Boca da Mata");
			mobi.setAttributeValue(iBocaDaMata, aLogradouro, "Fazenda Grande IV Setor 7 Caminho 58");
			mobi.setAttributeValue(iBocaDaMata, aBairro, "Boca da Mata");
			mobi.setAttributeValue(iBocaDaMata, aCep, "41.340-120");
			mobi.setAttributeValue(iBocaDaMata, aLatitude, "-12.9831037");
			mobi.setAttributeValue(iBocaDaMata, aLongitude, "-38.4452685");
			
			mobi.setAttributeValue(iCajazeirasIV, aNumero, "2");
			mobi.setAttributeValue(iCajazeirasIV, aNome, "USF Cajazeiras IV");
			mobi.setAttributeValue(iCajazeirasIV, aLogradouro, "Rua Alvaro Franca da Rocha");
			mobi.setAttributeValue(iCajazeirasIV, aBairro, "Cajazeiras");
			mobi.setAttributeValue(iCajazeirasIV, aCep, "41.334-320");
			mobi.setAttributeValue(iCajazeirasIV, aLatitude, "-12.9931037");
			mobi.setAttributeValue(iCajazeirasIV, aLongitude, "-38.4552685");
			
			mobi.setAttributeValue(iAcarbose, aCodigo, "1");
			mobi.setAttributeValue(iAcarbose, aPrincipio, "Acarbose");
			mobi.setAttributeValue(iAcarbose, aDosagem, "50mg");
			mobi.setAttributeValue(iAcarbose, aForma, "Comprimido");
			mobi.setAttributeValue(iAcarbose, aQuantidade, "0");
			
			mobi.setAttributeValue(iAcarbose, aCodigo, "2");
			mobi.setAttributeValue(iAcarbose, aPrincipio, "Adenosina");
			mobi.setAttributeValue(iAcarbose, aDosagem, "3mg/ml (2ml)");
			mobi.setAttributeValue(iAcarbose, aForma, "Solução injetável");
			mobi.setAttributeValue(iAcarbose, aQuantidade, "2");
			
			Relation rPossui = mobi.createUnidirecionalCompositionRelationship("possui");
			
			rPossui.setClassA(cPosto);
			rPossui.setClassB(cRemedio);
			rPossui.addInstanceRelation(iBocaDaMata, iAcarbose);
			rPossui.addInstanceRelation(iBocaDaMata, iAdenosina);
			rPossui.addInstanceRelation(iCajazeirasIV, iAcarbose);
			rPossui.processCardinality();
			mobi.addConcept(rPossui);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return mobi;
	}
}