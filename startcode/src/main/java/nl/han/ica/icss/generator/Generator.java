package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Generator {
	public String generate(AST ast) {
		return generateStyleSheet(ast.root);
	}

	private String generateStyleSheet(Stylesheet stylesheet){
		StringBuilder result = new StringBuilder();

		for (ASTNode astNode: stylesheet.getChildren()){
			if (astNode instanceof Stylerule){
				result.append(generateStyleRule((Stylerule) astNode));
			}
		}

		return result.toString();
	}

	private String generateDeclaration(Declaration declaration) {
		return "  " + declaration.property.name + ": " + toStringExpression(declaration.expression) + ";\n";
	}

	private String generateSelector(Stylerule stylerule){
		return stylerule.selectors.stream().map(ASTNode::toString).collect(Collectors.joining(" "));
	}

	private String generateStyleRule(Stylerule stylerule){
		return generateSelector(stylerule) + " {\n" + generateRuleBody(stylerule.body) + "}\n\n";
	}

	private String generateRuleBody(ArrayList<ASTNode> body){
		StringBuilder result = new StringBuilder();

		for (ASTNode astNode: body){
			if (astNode instanceof Declaration){
				result.append(generateDeclaration((Declaration) astNode));
			} else if (astNode instanceof IfClause){
				IfClause ifClause = (IfClause) astNode;

				for (ASTNode child: ifClause.body){
					if (child instanceof Declaration){
						result.append(generateDeclaration((Declaration) child));
					}
				}
			}
		}

		return result.toString();
	}

	private String toStringExpression(Expression expression){
		if (expression instanceof PercentageLiteral){
			return ((PercentageLiteral) expression).value + "%";
		} else if (expression instanceof PixelLiteral){
			return ((PixelLiteral) expression).value + "px";
		} else if (expression instanceof ColorLiteral){
			return ((ColorLiteral) expression).value;
		} else {
			return "";
		}
	}
}