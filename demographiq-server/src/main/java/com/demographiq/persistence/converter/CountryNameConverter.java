package com.demographiq.persistence.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Utility class for converting country codes to full country names
 * and vice versa.
 */

//https://developers.arcgis.com/rest/geocode/geocode-coverage/
//https://en.wikipedia.org/wiki/ISO_3166-2
@Component
public class CountryNameConverter {
    private static final Logger logger = LoggerFactory.getLogger(CountryNameConverter.class);

    private static final Map<String, String> COUNTRY_CODE_TO_NAME;

    /**
     * Converts a country code to its corresponding full name.
     *
     * @param countryCode The source country code (e.g., "US", "CA")
     * @return The full country name, or the original code if not found
     * @throws IllegalArgumentException if the country code is not found in the map
     */
    public String getCountryName(String countryCode) {
        if (countryCode == null) {
            throw new IllegalArgumentException("Country code cannot be null");
        }
        
        String upperCode = countryCode.trim().toUpperCase();
        if (!COUNTRY_CODE_TO_NAME.containsKey(upperCode)) {
            String errorMessage = "Unknown country code: " + upperCode;
            // Add logging (you'll need to add a logger field to the class)
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        return COUNTRY_CODE_TO_NAME.get(upperCode);
    }

    static {
        Map<String, String> codeToName = new HashMap<>();
        codeToName.put("WORLD", "Global");
        codeToName.put("AD", "Andorra");
        codeToName.put("AE", "United Arab Emirates");
        codeToName.put("AF", "Afghanistan");
        codeToName.put("AG", "Antigua and Barbuda");
        codeToName.put("AI", "Anguilla");
        codeToName.put("AL", "Albania");
        codeToName.put("AM", "Armenia");
        codeToName.put("AO", "Angola");
        codeToName.put("AQ", "Antarctica");
        codeToName.put("AR", "Argentina");
        codeToName.put("AS", "American Samoa");
        codeToName.put("AT", "Austria");
        codeToName.put("AU", "Australia");
        codeToName.put("AW", "Aruba");
        codeToName.put("AX", "Aland Islands");
        codeToName.put("AZ", "Azerbaijan");
        codeToName.put("BA", "Bosnia and Herzegovina");
        codeToName.put("BB", "Barbados");
        codeToName.put("BD", "Bangladesh");
        codeToName.put("BE", "Belgium");
        codeToName.put("BF", "Burkina Faso");
        codeToName.put("BG", "Bulgaria");
        codeToName.put("BH", "Bahrain");
        codeToName.put("BI", "Burundi");
        codeToName.put("BJ", "Benin");
        codeToName.put("BL", "Saint Barthélemy");
        codeToName.put("BM", "Bermuda");
        codeToName.put("BN", "Brunei Darussalam");
        codeToName.put("BO", "Bolivia");
        codeToName.put("BQ", "Bonaire, Sint Eustatius and Saba");
        codeToName.put("BR", "Brazil");
        codeToName.put("BS", "Bahamas");
        codeToName.put("BT", "Bhutan");
        codeToName.put("BV", "Bouvet Island");
        codeToName.put("BW", "Botswana");
        codeToName.put("BY", "Belarus");
        codeToName.put("BZ", "Belize");
        codeToName.put("CA", "Canada");
        codeToName.put("CC", "Cocos Islands");
        codeToName.put("CD", "Democratic Republic of the Congo");
        codeToName.put("CF", "Central African Republic");
        codeToName.put("CG", "Congo");
        codeToName.put("CH", "Switzerland");
        codeToName.put("CI", "Cote d'Ivoire");
        codeToName.put("CK", "Cook Islands");
        codeToName.put("CL", "Chile");
        codeToName.put("CM", "Cameroon");
        codeToName.put("CN", "China");
        codeToName.put("CO", "Colombia");
        codeToName.put("CR", "Costa Rica");
        codeToName.put("CU", "Cuba");
        codeToName.put("CV", "Cabo Verde");
        codeToName.put("CW", "Curacao");
        codeToName.put("CX", "Christmas Island");
        codeToName.put("CY", "Cyprus");
        codeToName.put("CZ", "Czechia");
        codeToName.put("DE", "Germany");
        codeToName.put("DJ", "Djibouti");
        codeToName.put("DK", "Denmark");
        codeToName.put("DM", "Dominica");
        codeToName.put("DO", "Dominican Republic");
        codeToName.put("DZ", "Algeria");
        codeToName.put("EC", "Ecuador");
        codeToName.put("EE", "Estonia");
        codeToName.put("EG", "Egypt");
        codeToName.put("EH", "Western Sahara");
        codeToName.put("ER", "Eritrea");
        codeToName.put("ES", "Spain");
        codeToName.put("ET", "Ethiopia");
        codeToName.put("FI", "Finland");
        codeToName.put("FJ", "Fiji");
        codeToName.put("FK", "Falkland Islands");
        codeToName.put("FM", "Micronesia");
        codeToName.put("FO", "Faroe Islands");
        codeToName.put("FR", "France");
        codeToName.put("GA", "Gabon");
        codeToName.put("GB", "United Kingdom");
        codeToName.put("GD", "Grenada");
        codeToName.put("GE", "Georgia");
        codeToName.put("GF", "French Guiana");
        codeToName.put("GG", "Guernsey");
        codeToName.put("GH", "Ghana");
        codeToName.put("GI", "Gibraltar");
        codeToName.put("GL", "Greenland");
        codeToName.put("GM", "Gambia");
        codeToName.put("GN", "Guinea");
        codeToName.put("GP", "Guadeloupe");
        codeToName.put("GQ", "Equatorial Guinea");
        codeToName.put("GR", "Greece");
        codeToName.put("GS", "South Georgia and the South Sandwich Islands");
        codeToName.put("GT", "Guatemala");
        codeToName.put("GU", "Guam");
        codeToName.put("GW", "Guinea-Bissau");
        codeToName.put("GY", "Guyana");
        codeToName.put("HK", "Hong Kong");
        codeToName.put("HM", "Heard Island and McDonald Islands");
        codeToName.put("HN", "Honduras");
        codeToName.put("HR", "Croatia");
        codeToName.put("HT", "Haiti");
        codeToName.put("HU", "Hungary");
        codeToName.put("ID", "Indonesia");
        codeToName.put("IE", "Ireland");
        codeToName.put("IL", "Israel");
        codeToName.put("IM", "Isle of Man");
        codeToName.put("IN", "India");
        codeToName.put("IO", "British Indian Ocean Territory");
        codeToName.put("IQ", "Iraq");
        codeToName.put("IR", "Iran");
        codeToName.put("IS", "Iceland");
        codeToName.put("IT", "Italy");
        codeToName.put("JE", "Jersey");
        codeToName.put("JM", "Jamaica");
        codeToName.put("JO", "Jordan");
        codeToName.put("JP", "Japan");
        codeToName.put("KE", "Kenya");
        codeToName.put("KG", "Kyrgyzstan");
        codeToName.put("KH", "Cambodia");
        codeToName.put("KI", "Kiribati");
        codeToName.put("KM", "Comoros");
        codeToName.put("KN", "Saint Kitts and Nevis");
        codeToName.put("KP", "North Korea");
        codeToName.put("KR", "South Korea");
        codeToName.put("KW", "Kuwait");
        codeToName.put("KY", "Cayman Islands");
        codeToName.put("KZ", "Kazakhstan");
        codeToName.put("LA", "Lao People's Democratic Republic");
        codeToName.put("LB", "Lebanon");
        codeToName.put("LC", "Saint Lucia");
        codeToName.put("LI", "Liechtenstein");
        codeToName.put("LK", "Sri Lanka");
        codeToName.put("LR", "Liberia");
        codeToName.put("LS", "Lesotho");
        codeToName.put("LT", "Lithuania");
        codeToName.put("LU", "Luxembourg");
        codeToName.put("LV", "Latvia");
        codeToName.put("LY", "Libya");
        codeToName.put("MA", "Morocco");
        codeToName.put("MC", "Monaco");
        codeToName.put("MD", "Moldova");
        codeToName.put("ME", "Montenegro");
        codeToName.put("MF", "Saint Martin");
        codeToName.put("MG", "Madagascar");
        codeToName.put("MH", "Marshall Islands");
        codeToName.put("MK", "North Macedonia");
        codeToName.put("ML", "Mali");
        codeToName.put("MM", "Myanmar");
        codeToName.put("MN", "Mongolia");
        codeToName.put("MO", "Macao");
        codeToName.put("MP", "Northern Mariana Islands");
        codeToName.put("MQ", "Martinique");
        codeToName.put("MR", "Mauritania");
        codeToName.put("MS", "Montserrat");
        codeToName.put("MT", "Malta");
        codeToName.put("MU", "Mauritius");
        codeToName.put("MV", "Maldives");
        codeToName.put("MW", "Malawi");
        codeToName.put("MX", "Mexico");
        codeToName.put("MY", "Malaysia");
        codeToName.put("MZ", "Mozambique");
        codeToName.put("NA", "Namibia");
        codeToName.put("NC", "New Caledonia");
        codeToName.put("NE", "Niger");
        codeToName.put("NF", "Norfolk Island");
        codeToName.put("NG", "Nigeria");
        codeToName.put("NI", "Nicaragua");
        codeToName.put("NL", "Netherlands");
        codeToName.put("NO", "Norway");
        codeToName.put("NP", "Nepal");
        codeToName.put("NR", "Nauru");
        codeToName.put("NU", "Niue");
        codeToName.put("NZ", "New Zealand");
        codeToName.put("OM", "Oman");
        codeToName.put("PA", "Panama");
        codeToName.put("PE", "Peru");
        codeToName.put("PF", "French Polynesia");
        codeToName.put("PG", "Papua New Guinea");
        codeToName.put("PH", "Philippines");
        codeToName.put("PK", "Pakistan");
        codeToName.put("PL", "Poland");
        codeToName.put("PM", "Saint Pierre and Miquelon");
        codeToName.put("PN", "Pitcairn");
        codeToName.put("PR", "Puerto Rico");
        codeToName.put("PS", "Palestine");
        codeToName.put("PT", "Portugal");
        codeToName.put("PW", "Palau");
        codeToName.put("PY", "Paraguay");
        codeToName.put("QA", "Qatar");
        codeToName.put("RE", "Réunion");
        codeToName.put("RO", "Romania");
        codeToName.put("RS", "Serbia");
        codeToName.put("RU", "Russia");
        codeToName.put("RW", "Rwanda");
        codeToName.put("SA", "Saudi Arabia");
        codeToName.put("SB", "Solomon Islands");
        codeToName.put("SC", "Seychelles");
        codeToName.put("SD", "Sudan");
        codeToName.put("SE", "Sweden");
        codeToName.put("SG", "Singapore");
        codeToName.put("SH", "Saint Helena, Ascension and Tristan da Cunha");
        codeToName.put("SI", "Slovenia");
        codeToName.put("SJ", "Svalbard and Jan Mayen");
        codeToName.put("SK", "Slovakia");
        codeToName.put("SL", "Sierra Leone");
        codeToName.put("SM", "San Marino");
        codeToName.put("SN", "Senegal");
        codeToName.put("SO", "Somalia");
        codeToName.put("SR", "Suriname");
        codeToName.put("SS", "South Sudan");
        codeToName.put("ST", "Sao Tome and Principe");
        codeToName.put("SV", "El Salvador");
        codeToName.put("SX", "Sint Maarten");
        codeToName.put("SY", "Syrian Arab Republic");
        codeToName.put("SZ", "Eswatini");
        codeToName.put("TC", "Turks and Caicos Islands");
        codeToName.put("TD", "Chad");
        codeToName.put("TF", "French Southern Territories");
        codeToName.put("TG", "Togo");
        codeToName.put("TH", "Thailand");
        codeToName.put("TJ", "Tajikistan");
        codeToName.put("TK", "Tokelau");
        codeToName.put("TL", "Timor-Leste");
        codeToName.put("TM", "Turkmenistan");
        codeToName.put("TN", "Tunisia");
        codeToName.put("TO", "Tonga");
        codeToName.put("TR", "Türkiye");
        codeToName.put("TT", "Trinidad and Tobago");
        codeToName.put("TV", "Tuvalu");
        codeToName.put("TW", "Taiwan");
        codeToName.put("TZ", "Tanzania");
        codeToName.put("UA", "Ukraine");
        codeToName.put("UG", "Uganda");
        codeToName.put("UM", "United States Minor Outlying Islands");
        codeToName.put("US", "United States");
        codeToName.put("UY", "Uruguay");
        codeToName.put("UZ", "Uzbekistan");
        codeToName.put("VA", "Holy See");
        codeToName.put("VC", "Saint Vincent and the Grenadines");
        codeToName.put("VE", "Venezuela");
        codeToName.put("VG", "Virgin Islands British");
        codeToName.put("VI", "Virgin Islands U.S.");
        codeToName.put("VN", "Vietnam");
        codeToName.put("VU", "Vanuatu");
        codeToName.put("WF", "Wallis and Futuna");
        codeToName.put("WS", "Samoa");
        codeToName.put("YE", "Yemen");
        codeToName.put("YT", "Mayotte");
        codeToName.put("ZA", "South Africa");
        codeToName.put("ZM", "Zambia");
        codeToName.put("ZW", "Zimbabwe");
        COUNTRY_CODE_TO_NAME = Collections.unmodifiableMap(codeToName);
    }
    

}