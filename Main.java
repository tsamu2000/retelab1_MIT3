package hu.bme.mit.yakindu.analysis.workhere;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.yakindu.sct.model.sgraph.State;
import org.yakindu.sct.model.sgraph.Statechart;
import org.yakindu.sct.model.sgraph.Transition;
import org.yakindu.sct.model.stext.stext.EventDefinition;
import org.yakindu.sct.model.stext.stext.VariableDefinition;

import hu.bme.mit.model2gml.Model2GML;
import hu.bme.mit.yakindu.analysis.RuntimeService;
import hu.bme.mit.yakindu.analysis.TimerService;
import hu.bme.mit.yakindu.analysis.example.ExampleStatemachine;
import hu.bme.mit.yakindu.analysis.example.IExampleStatemachine;
import hu.bme.mit.yakindu.analysis.modelmanager.ModelManager;

public class Main {
	@Test
	public void test() {
		main(new String[0]);
	}
	
	public static void main(String[] args) {
		ModelManager manager = new ModelManager();
		Model2GML model2gml = new Model2GML();
		
		// Loading model
		EObject root = manager.loadModel("model_input/example.sct");
		
		// Reading model
		Statechart s = (Statechart) root;
		TreeIterator<EObject> iterator = s.eAllContents();
		int j = 0;
		while (iterator.hasNext()) {
			EObject content = iterator.next();
			if(content instanceof State) {
				State state = (State) content;
				EList<Transition> statetransitions = state.getOutgoingTransitions();
				System.out.println(state.getName());
				
				for(int i=0;i<statetransitions.size();i++)
				{
					System.out.println(statetransitions.get(i).getSource().getName()+" -> "+statetransitions.get(i).getTarget().getName());
				}
				
				if(statetransitions.size()==0) 
				{
					System.out.println(state.getName()+" egy csapda.");
				}
				
				if (state.getName()=="")
				{
					System.out.println("Nincs neve az allapotnak. Javaslat: Allapot"+j+".");
				}
			}
			
			if (content instanceof EventDefinition) 
			{
				EventDefinition event = (EventDefinition) content;
				System.out.println("Event: "+event.getName()+".");
			}
			
			if (content instanceof VariableDefinition) 
			{
				VariableDefinition variable = (VariableDefinition) content;
				System.out.println("Variable: "+variable.getName()+".");
			}
		}
		
		ExampleStatemachine e = new ExampleStatemachine();
		e.setTimer(new TimerService());
		RuntimeService.getInstance().registerStatemachine(e, 200);
		e.init();
		e.enter();
		e.runCycle();
		
		printFunc(e);
		
		// Transforming the model into a graph representation
		String content = model2gml.transform(root);
		// and saving it
		manager.saveFile("model_output/graph.gml", content);
	}
	
	public static void printFunc(IExampleStatemachine e) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			line = br.readLine();
			while (!line.equals("exit")) 
			{
				System.out.println("public static void print(IExampleStatemachine s) {");
				System.out.println("System.out.println(\" "+line.substring(0, 1).toUpperCase()+" = \" + s.getSCInterface().get"
				+line.substring(0, 1).toUpperCase()+line.substring(1)+"());");
				System.out.println("}");
				line = br.readLine();
			}
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
