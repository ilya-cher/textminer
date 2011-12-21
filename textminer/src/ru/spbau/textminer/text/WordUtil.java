package ru.spbau.textminer.text;

import ru.spbau.textminer.text.feature.Case;
import ru.spbau.textminer.text.feature.Link;

import java.util.HashMap;
import java.util.Map;

public class WordUtil {
    private WordUtil() {}

    public static Link getLink(String linkStr) {
        return linkMap.get(linkStr);
    }

    private static Map<String, Link> linkMap = new HashMap<String, Link>();
    static {
        //used in the files and have descriptions
        linkMap.put("5-компл", Link.COMPL);           linkMap.put("сравнит", Link.COMP);
        linkMap.put("дистанц", Link.DIST);              linkMap.put("дат-субъект", Link.DAT_SUBJ);
        linkMap.put("колич-вспом", Link.NUM_AUX);       linkMap.put("предик", Link.PRED);
        linkMap.put("суб-копр", Link.SUBJ_COPRED);      linkMap.put("2-несобст-компл", Link.N_COMPL);
        linkMap.put("релят", Link.REL);                 linkMap.put("примыкат", Link.ADJOIN);
        linkMap.put("кратно-длительн", Link.REP_TIME);  linkMap.put("1-компл", Link.COMPL);
        linkMap.put("изъясн", Link.IZ);                 linkMap.put("огранич", Link.CONSTR);
        linkMap.put("колич-огран", Link.NUM_CONSTR);    linkMap.put("квазиагент", Link.Q_AGENT);
        linkMap.put("разъяснит", Link.RAZ);             linkMap.put("эксплет", Link.EXPL);
        linkMap.put("аппоз", Link.APP);                 linkMap.put("компл-аппоз", Link.COMPL_APP);
        linkMap.put("эллипт", Link.ELPT);               linkMap.put("обст-тавт", Link.ADVMOD_TAUT);
        linkMap.put("об-обст", Link.OBJ_ADVMOD );       linkMap.put("сент-предик", Link.SENT_PRED);
        linkMap.put("пролепт", Link.PL);                linkMap.put("вспом", Link.AUX);
        linkMap.put("колич-копред", Link.NUM_COPRED);   linkMap.put("3-компл", Link.COMPL);
        linkMap.put("обст", Link.ADVMOD);               linkMap.put("кратн", Link.REP);
        linkMap.put("неакт-компл", Link.NA_COMPL);      linkMap.put("аппрокс-колич", Link.APPROX_NUM);
        linkMap.put("сент-соч", Link.SENT_S);           linkMap.put("аддит", Link.ADD);
        linkMap.put("об-копр", Link.OBJ_COPRED);        linkMap.put("аналит", Link.AN);
        linkMap.put("суб-обст", Link.SUBJ_ADVMOD);      linkMap.put("соч-союзн", Link.S_CONJ);
        linkMap.put("предл", Link.PREP);                linkMap.put("несобст-агент", Link.N_AGENT );
        linkMap.put("распред", Link.DISTR);             linkMap.put("оп-опред", Link.DESCR_DEF);
        linkMap.put("пасс-анал", Link.PAN);             linkMap.put("об-аппоз", Link.D_APP);
        linkMap.put("1-несобст-компл", Link.N_COMPL);   linkMap.put("подч-союзн", Link.P_CONJ);
        linkMap.put("3-несобст-компл", Link.N_COMPL);   linkMap.put("уточн", Link.SPEC);
        linkMap.put("композ", Link.COMPOSITE);          linkMap.put("агент", Link.AGENT);
        linkMap.put("сочин", Link.S);                   linkMap.put("сравн-союзн", Link.COMP_CONJ);
        linkMap.put("соотнос", Link.CORR);              linkMap.put("нум-аппоз", Link.NUM_APP);
        linkMap.put("ном-аппоз", Link.NOM_APP);         linkMap.put("адр-присв", Link.ADR_LINK);
        linkMap.put("присвяз", Link.LINK);              linkMap.put("2-компл", Link.COMPL);
        linkMap.put("опред", Link.DEF);                 linkMap.put("длительн", Link.TIME);
        linkMap.put("атриб", Link.ATTR);                linkMap.put("ком-сочин", Link.COMM_S);
        linkMap.put("4-компл", Link.COMPL);             linkMap.put("вводн", Link.INTR);
        linkMap.put("инф-союзн", Link.INF_CONJ);        linkMap.put("количест", Link.NUM);

        //not used in the files and have descriptions:
        linkMap.put("элект", Link.ELECT);
        linkMap.put("аппрокс-порядк", Link.APPROX_ORD);

        //used in the files but have no descriptions:
        //linkMap.put("смещ-атриб", Link.UNKNOWN);
        linkMap.put("электив", Link.ELECT);     //most likely it is "элект"
        //linkMap.put("сравн-аппоз", Link.UNKNOWN);
        //linkMap.put("композ-аппоз", Link.UNKNOWN);
        //linkMap.put("оп-аппоз", Link.UNKNOWN);
        //linkMap.put("квазиобст", Link.UNKNOWN);
        //linkMap.put("авт-аппоз", Link.UNKNOWN);
        //linkMap.put("презентат", Link.UNKNOWN);
    }
}
