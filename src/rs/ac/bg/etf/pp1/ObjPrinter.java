package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;
import java.util.Collection;

public class ObjPrinter {

    public static String objToString(Obj obj) {
        StringBuilder sb = new StringBuilder();
        buildString(obj, 0, sb);
        return sb.toString();
    }

    private static void buildString(Obj obj, int level, StringBuilder sb) {
        if (obj == null) {
            sb.append("null\n");
            return;
        }

        String prefix = indent(level);

        sb.append(prefix).append("Obj {\n");
        sb.append(prefix).append("  name  = ").append(obj.getName()).append("\n");
        sb.append(prefix).append("  kind  = ").append(kindToString(obj.getKind())).append("\n");
        sb.append(prefix).append("  type  = ").append(structToString(obj.getType())).append("\n");
        sb.append(prefix).append("  adr   = ").append(obj.getAdr()).append("\n");
        sb.append(prefix).append("  level = ").append(obj.getLevel()).append("\n");
        sb.append(prefix).append("  fpPos = ").append(obj.getFpPos()).append("\n");

        Collection<Obj> locals = obj.getLocalSymbols();
        if (!locals.isEmpty()) {
            sb.append(prefix).append("  locals = [\n");
            for (Obj local : locals) {
                buildString(local, level + 2, sb);
            }
            sb.append(prefix).append("  ]\n");
        }

        sb.append(prefix).append("}\n");
    }

    private static String indent(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private static String kindToString(int kind) {
        switch (kind) {
            case Obj.Con:  return "Con";
            case Obj.Var:  return "Var";
            case Obj.Type: return "Type";
            case Obj.Meth: return "Meth";
            case Obj.Fld:  return "Fld";
            case Obj.Elem: return "Elem";
            case Obj.Prog: return "Prog";
            default:       return "Unknown(" + kind + ")";
        }
    }

    private static String structToString(Struct struct) {
        if (struct == null) return "null";

        switch (struct.getKind()) {
            case Struct.None:      return "None";
            case Struct.Int:       return "int";
            case Struct.Char:      return "char";
            case Struct.Bool:      return "bool";
            case Struct.Array:     return "Array of " + structToString(struct.getElemType());
            case Struct.Class:     return classStructToString(struct);
            case Struct.Enum:      return "Enum";
            case Struct.Interface: return "Interface";
            default:               return "UnknownStruct(" + struct.getKind() + ")";
        }
    }

    private static String classStructToString(Struct struct) {
        StringBuilder sb = new StringBuilder();
        sb.append("Class { fields=").append(struct.getNumberOfFields());
        Collection<Obj> members = struct.getMembers();
        if (!members.isEmpty()) {
            sb.append(", members=[");
            boolean first = true;
            for (Obj member : members) {
                if (!first) sb.append(", ");
                sb.append(member.getName()).append(":").append(kindToString(member.getKind()));
                first = false;
            }
            sb.append("]");
        }
        sb.append(" }");
        return sb.toString();
    }
}