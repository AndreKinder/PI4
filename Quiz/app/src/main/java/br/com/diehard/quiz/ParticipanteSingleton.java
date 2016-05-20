package br.com.diehard.quiz;


public class ParticipanteSingleton {

    private static ParticipanteSingleton Instance = new ParticipanteSingleton();

    public String nome;
    public String email;
    public Integer codParticipante;


    public static ParticipanteSingleton getInstance()
    {
        return Instance;
    }

    private ParticipanteSingleton() {

    }
}
