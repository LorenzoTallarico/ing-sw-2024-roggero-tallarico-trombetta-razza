package it.polimi.ingsw.model;
import com.google.gson.*;
import java.lang.reflect.Type;

public class StrategyInstanceCreator implements InstanceCreator<Strategy>{
    private final String strategyType;

    // Costruttore che accetta il tipo di strategia da creare
    public StrategyInstanceCreator(String strategyType) {
        this.strategyType = strategyType;
    }

    //In questa maniera nel main dove verrà creato il gioco o dove necessario si dovrà chiamare passando nel costruttore esattamente il
    // nome dello strategy che si vuole usare come stringa (si può forse migliorare), per ora usiamo questi:
//    "diagonal"
//    "item"
//    "lshape"
//    "mixed"
//    "resource"
//
    @Override
    public Strategy createInstance(Type type){
        //confronto switch equivale a "equals"
        switch(strategyType) {
            case "diagonal":
                return new ConcreteStrategyDiagonal();
            case "item":
                return new ConcreteStrategyItem();
            case "lshape":
                return new ConcreteStrategyLshape();
            case "mixed":
                return new ConcreteStrategyMixed();
            case "resource":
                return new ConcreteStrategyResource();
            default:
                throw new IllegalArgumentException("Tipo di strategia non supportato: " + strategyType);
        }
    }

}
