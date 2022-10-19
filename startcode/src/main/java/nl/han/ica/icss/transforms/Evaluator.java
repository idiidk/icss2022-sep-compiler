package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.SymbolTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

public class Evaluator implements Transform {
    private SymbolTable<String, Literal> variableValues;

    @Override
    public void apply(AST ast) {
        variableValues = new SymbolTable<>();

        variableValues.pushScope();
        recursiveChildTransform(ast.root);
        variableValues.popScope();
    }

    public void transformNode(ASTNode node, ASTNode parentNode) {
        if (node instanceof Stylerule) {
            variableValues.pushScope();
            transformStylerule((Stylerule) node);
            variableValues.popScope();
        } else if (node instanceof VariableAssignment) {
            transformVariableAssignment((VariableAssignment) node);
        } else if (node instanceof Declaration) {
            transformDeclaration((Declaration) node);
        } else if (node instanceof IfClause) {
            transformIfClause((IfClause) node, parentNode);
        }
    }
    
    public void recursiveChildTransform(ASTNode parentNode) {
        for (ASTNode child : parentNode.getChildren()) {
            transformNode(child, parentNode);
        }
    }

    // Remove duplicate declarations
    public void transformStylerule(Stylerule stylerule) {
        recursiveChildTransform(stylerule);

        Stylerule transformedStylerule = new Stylerule();
        HashMap<String, Declaration> seenDeclarations = new HashMap<>();
        for (ASTNode child : stylerule.body) {
            if(child instanceof Declaration) {
                Declaration original = (Declaration) child;
                Declaration seenDeclaration = seenDeclarations.get(original.property.name);

                if(seenDeclaration != null) {
                    seenDeclarations.remove(seenDeclaration);
                    transformedStylerule.removeChild(seenDeclaration);
                }

                seenDeclarations.put(original.property.name, original);
                transformedStylerule.addChild(original);
                continue;
            }

            transformedStylerule.addChild(child);
        }

        stylerule.body = transformedStylerule.body;
    }

    public void transformVariableAssignment(VariableAssignment assignment) {
        assignment.expression = evaluateExpression(assignment.expression);
        variableValues.putVariable(assignment.name.name, (Literal) assignment.expression);
    }

    public void transformDeclaration(Declaration declaration) {
        declaration.expression = evaluateExpression(declaration.expression);
    }

    public void transformIfClause(IfClause clause, ASTNode parent) {
        BoolLiteral result = (BoolLiteral) evaluateExpression(clause.conditionalExpression);

        if (result.value) {
            // Swap the body of the if statement with the parent node after it was parsed
            clause.elseClause = null;
        } else {
            if (clause.elseClause != null) {
                clause.body = clause.elseClause.body;
            } else {
                clause.body = new ArrayList<>();
            }
        }

        recursiveChildTransform(clause);

        for (ASTNode child : clause.body) {
            parent.addChild(child);
        }

        parent.removeChild(clause);
    }

    public Literal evaluateExpression(Expression expression) {
        if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }

        if (expression instanceof VariableReference) {
            return variableValues.getVariable(((VariableReference) expression).name);
        }

        return (Literal) expression;
    }

    public Literal evaluateOperation(Operation operation) {
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);

        int leftValue = (int) left.getValue();
        int rightValue = (int) right.getValue();

        if (operation instanceof MultiplyOperation) {
            return cloneLiteralWithValue(left, leftValue * rightValue);
        }

        if (operation instanceof AddOperation) {
            return cloneLiteralWithValue(left, leftValue + rightValue);
        }

        if (operation instanceof SubtractOperation) {
            return cloneLiteralWithValue(left, leftValue - rightValue);
        }

        throw new RuntimeException("Evaluation type not found");
    }

    private Literal cloneLiteralWithValue(Literal literal, int value) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        }

        if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        }

        if (literal instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        }

        throw new RuntimeException("Literal to clone not found");
    }
}
