package ru.spbau.textminer.text.feature;

import ru.spbau.textminer.text.InvalidFeatureException;
import ru.spbau.textminer.text.WordUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Features {
    public Features(String featStr) throws InvalidFeatureException {
        Scanner sc = new Scanner(featStr);
        while (sc.hasNext()) {
            String feat = sc.next();
            if (animacyMap.get(feat) != null) {
                animacy = animacyMap.get(feat);
            }
            if (aspectMap.get(feat) != null) {
                aspect = aspectMap.get(feat);
            }
            if (caseMap.get(feat) != null) {
                gramCase = caseMap.get(feat);
            }
            if (compDegreeMap.get(feat) != null) {
                degree = compDegreeMap.get(feat);
            }
            if (genderMap.get(feat) != null) {
                gender = genderMap.get(feat);
            }
            if (moodMap.get(feat) != null) {
                mood = moodMap.get(feat);
            }
            if (numberMap.get(feat) != null) {
                number = numberMap.get(feat);
            }
            if (personMap.get(feat) != null) {
                person = personMap.get(feat);
            }
            if (posMap.get(feat) != null) {
                pos = posMap.get(feat);
            }
            if (tenseMap.get(feat) != null) {
                tense = tenseMap.get(feat);
            }
            if (verbFormMap.get(feat) != null) {
                form = verbFormMap.get(feat);
            }
            if (voiceMap.get(feat) != null) {
                voice = voiceMap.get(feat);
            }
        }

        if (voice == null && pos == POS.V) {
            voice = Voice.ACTIVE;
        }
        if (form == null && pos == POS.V) {
            form = VerbForm.INF;
        }
        if (degree == null && pos == POS.A) {
            degree = CompDegree.POSITIVE;
        }
    }

    public POS getPOS() { return pos; }
    public Number getNumber() { return number; }
    public Gender getGender() { return gender; }
    public Tense getTense() { return tense; }
    public Voice getVoice() { return voice; }
    public Case getCase() { return gramCase; }
    public Animacy getAnimacy() { return animacy; }
    public Person getPerson() { return person; }
    public CompDegree getDegree() { return degree; }
    public VerbForm getVerbForm() { return form; }
    public Aspect getAspect() { return aspect; }
    public Mood getMood() { return mood; }

    private POS pos;
    private Number number;
    private Gender gender;
    private Tense tense;
    private Voice voice;
    private Case gramCase;
    private Animacy animacy;
    private Person person;
    private CompDegree degree;
    private VerbForm form;
    private Aspect aspect;
    private Mood mood;

    private static Map<String, Animacy> animacyMap = new HashMap<String, Animacy>();
    private static Map<String, POS> posMap = new HashMap<String, POS>();
    private static Map<String, Aspect> aspectMap = new HashMap<String, Aspect>();
    private static Map<String, Case> caseMap = new HashMap<String, Case>();
    private static Map<String, CompDegree> compDegreeMap = new HashMap<String, CompDegree>();
    private static Map<String, Gender> genderMap = new HashMap<String, Gender>();
    private static Map<String, Mood> moodMap = new HashMap<String, Mood>();
    private static Map<String, Number> numberMap = new HashMap<String, Number>();
    private static Map<String, Person> personMap = new HashMap<String, Person>();
    private static Map<String, Tense> tenseMap = new HashMap<String, Tense>();
    private static Map<String, VerbForm> verbFormMap = new HashMap<String, VerbForm>();
    private static Map<String, Voice> voiceMap = new HashMap<String, Voice>();

    static {
        moodMap.put("ИЗЪЯВ", Mood.INDICATIVE);
        moodMap.put("ПОВ", Mood.IMPERATIVE);

        animacyMap.put("ОД", Animacy.ANIMATE);
        animacyMap.put("НЕОД", Animacy.INANIMATE);

        posMap.put("INTJ", POS.INTJ);
        posMap.put("PART", POS.PART);
        posMap.put("ADV", POS.ADV);
        posMap.put("COM", POS.COM);
        posMap.put("PR", POS.PR);
        posMap.put("A", POS.A);
        posMap.put("V", POS.V);
        posMap.put("P", POS.P);
        posMap.put("S", POS.S);
        posMap.put("NUM", POS.NUM);
        posMap.put("CONJ", POS.CONJ);
        posMap.put("NID", POS.NID);

        aspectMap.put("НЕСОВ", Aspect.IMPERFECTIVE);
        aspectMap.put("СОВ", Aspect.PERFECTIVE);

        verbFormMap.put("ПРИЧ", VerbForm.PART);
        verbFormMap.put("ИНФ", VerbForm.INF);
        verbFormMap.put("ДЕЕПР", VerbForm.ADVPART);

        compDegreeMap.put("ПРЕВ", CompDegree.SUPERLATIVE);
        compDegreeMap.put("СРАВ", CompDegree.COMPARATIVE);

        personMap.put("3-Л", Person.THIRD);
        personMap.put("2-Л", Person.SECOND);
        personMap.put("1-Л", Person.FIRST);

        caseMap.put("РОД", Case.GEN);
        caseMap.put("ИМ", Case.NOM);
        caseMap.put("ПР", Case.PREP);
        caseMap.put("ВИН", Case.ACC);
        caseMap.put("ТВОР", Case.INS);
        caseMap.put("ДАТ", Case.DAT);
        caseMap.put("МЕСТН", Case.LOC);
        caseMap.put("ЗВ", Case.VOC);
        caseMap.put("ПАРТ", Case.PART);

        voiceMap.put("СТРАД", Voice.PASSIVE);

        numberMap.put("МН", Number.PLURAL);
        numberMap.put("ЕД", Number.SINGULAR);

        tenseMap.put("ПРОШ", Tense.PAST);
        tenseMap.put("НАСТ", Tense.PRESENT);
        tenseMap.put("НЕПРОШ", Tense.NOT_PAST);

        genderMap.put("МУЖ", Gender.M);
        genderMap.put("ЖЕН", Gender.F);
        genderMap.put("СРЕД", Gender.N);
    }
}
