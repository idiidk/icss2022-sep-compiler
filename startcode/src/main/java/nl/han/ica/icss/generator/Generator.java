package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

import java.util.stream.Collectors;

public class Generator {
    public String generate(AST ast) {
        return recursiveChildGenerate(ast.root);
    }

    private String recursiveChildGenerate(ASTNode parentNode) {
        StringBuilder total = new StringBuilder();

        for (ASTNode child : parentNode.getChildren()) {
            if (child instanceof Stylerule) {
                total.append(generateStylerule((Stylerule) child));
            } else if (child instanceof Declaration) {
                total.append(generateDeclaration((Declaration) child));
            } else if (child instanceof IfClause) {
                total.append(recursiveChildGenerate(child));
            }
        }

        return total.toString();
    }

    private String generateDeclaration(Declaration declaration) {
        StringBuilder result = new StringBuilder();

        result.append("  ");
        result.append(declaration.property.name);
        result.append(": ");
        result.append(toStringExpression(declaration.expression));
        result.append(";\n");

        return result.toString();
    }

    private String generateStylerule(Stylerule stylerule) {
        StringBuilder result = new StringBuilder();

        result.append(stylerule.selectors.stream().map(ASTNode::toString).collect(Collectors.joining(", ")));
        result.append(" {\n");
        result.append(recursiveChildGenerate(stylerule));
        result.append("}\n\n");

        return result.toString();
    }

    private String toStringExpression(Expression expression) {
        if (expression instanceof PercentageLiteral) {
            return ((PercentageLiteral) expression).value + "%";
        } else if (expression instanceof PixelLiteral) {
            return ((PixelLiteral) expression).value + "px";
        } else if (expression instanceof ColorLiteral) {
            return ((ColorLiteral) expression).value;
        } else {
            return "";
        }
    }
}