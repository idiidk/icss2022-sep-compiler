package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.SymbolTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;

public class Checker {
    private SymbolTable<String, ExpressionType> variableTypes;

    public void check(AST ast) {
        variableTypes = new SymbolTable<>();

        checkNode(ast.root);
    }

    private void checkNode(ASTNode node) {
        if (node instanceof Stylesheet) {
            checkStylesheet((Stylesheet) node);
        } else if (node instanceof Stylerule) {
            checkStylerule((Stylerule) node);
        } else if (node instanceof Declaration) {
            checkDeclaration((Declaration) node);
        } else if (node instanceof VariableAssignment) {
            checkVariableAssignment((VariableAssignment) node);
        } else if (node instanceof VariableReference) {
            checkVariableReference((VariableReference) node);
        } else if (node instanceof IfClause) {
            checkIfClause((IfClause) node);
        } else if (node instanceof ElseClause) {
            checkElseClause((ElseClause) node);
        } else {
            System.out.println("Check not implemented for node of type: " + node.getNodeLabel());
        }
    }

    // For scoped rules, just check all the children in the body of the scope
    // separately. Loop over the children and recursively continue checking.
    private void checkStylesheet(Stylesheet node) {
        variableTypes.pushScope();
        for (ASTNode child : node.body) {
            checkNode(child);
        }
        variableTypes.popScope();
    }

    private void checkStylerule(Stylerule node) {
        variableTypes.pushScope();
        for (ASTNode child : node.body) {
            checkNode(child);
        }
        variableTypes.popScope();
    }

    public void checkIfClause(IfClause node) {
        // Make sure to check the else clause aswell
        if(node.elseClause != null) {
            checkNode(node.elseClause);
        }

        // Check the conditional expression of the if statement
        checkNode(node.conditionalExpression);

        variableTypes.pushScope();
        for (ASTNode child : node.body) {
            checkNode(child);
        }
        variableTypes.popScope();
    }

    public void checkElseClause(ElseClause node) {
        variableTypes.pushScope();
        for (ASTNode child : node.body) {
            checkNode(child);
        }
        variableTypes.popScope();
    }

    private void checkDeclaration(Declaration node) {
        ExpressionType expressionType = getExpressionType(node.expression);

        // Check if expression type matches for the property being declared:
        switch (node.property.name) {
            case "width":
            case "height":
                if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                    node.setError("Size properties should be assigned a pixel literal or a percentage literal");
                    return;
                }
                break;
            case "color":
            case "background-color":
                if (expressionType != ExpressionType.COLOR) {
                    node.setError("Color properties should be assigned a color literal");
                    return;
                }
                break;
            default:
                node.setError("Unknown property, choose from width, height, color or background-color");
                break;
        }
    }

    private void checkVariableAssignment(VariableAssignment node) {
        String newVariableName = node.name.name; // // Variable reference name (very nice naming scheme I know)

        ExpressionType newExpressionType = getExpressionType(node.expression);
        ExpressionType previousExpressionType = variableTypes.getVariable(newVariableName);

        if (previousExpressionType != null && newExpressionType != previousExpressionType) {
            node.setError("Variable type invalid, expected: " + previousExpressionType.name() + " but found: " + newExpressionType.name());
            return;
        }

        variableTypes.putVariable(newVariableName, newExpressionType);
    }

    public void checkVariableReference(VariableReference node) {
        ExpressionType expressionType = variableTypes.getVariable(node.name);
        if (expressionType == null) {
            node.setError("Variable: " + node.name + " not defined in scope");
        }
    }

    // TODO: handle if / else clauses


    // TODO: make the checkOperation handler

    public ExpressionType getExpressionType(Expression expression) {
        // If the expression contains an operation, unpack that bitch
        // and return the eventual left type. Left type should be the same as
        // the right type, or we'll error in the checkOperation checker.

        // FOOL PROOF I SAY :D
        if (expression instanceof Operation) {
            return getExpressionType(((Operation) expression).lhs);
        }

        // If the expression contains a variable reference
        // check if the reference is valid and set the error on the node if so.
        // This error is used to determine if the variable is set, and we'll return accordingly
        if (expression instanceof VariableReference) {
            checkVariableReference((VariableReference) expression);
            if (expression.hasError()) {
                return ExpressionType.UNDEFINED;
            } else {
                return variableTypes.getVariable(((VariableReference) expression).name);
            }
        }

        // Finally something easy, return the enum entry corresponding to the literal
        if (expression instanceof PixelLiteral) {
            return ExpressionType.PIXEL;
        }

        if (expression instanceof ColorLiteral) {
            return ExpressionType.COLOR;
        }

        if (expression instanceof ScalarLiteral) {
            return ExpressionType.SCALAR;
        }

        if (expression instanceof BoolLiteral) {
            return ExpressionType.BOOL;
        }

        if (expression instanceof PercentageLiteral) {
            return ExpressionType.PERCENTAGE;
        }

        return ExpressionType.UNDEFINED;
    }
}
