package pokerBase;

import java.util.ArrayList;
import java.util.*;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import pokerEnums.eGame;
import pokerEnums.eRank;
import pokerEnums.eSuit;

import java.io.StringReader;
import java.io.StringWriter;

import org.hibernate.HibernateException; 
import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import javax.xml.bind.annotation.XmlElement;

public class PlayHand {
	//Start Hibernate
	private static SessionFactory factory;

	public static void main(String[] args) {
		try{
			factory = new AnnotationConfiguration().
			configure().
			//addPackage("com.xyz") //add package if there is an error about missing package xyz
			addAnnotatedClass(Hand.class)
			buildSessionFactory();
		}catch (Throwable ex) {
			System.err.println("Failed to create sessionFactory object." + ex);
			throw new ExceptionInInitializerError(ex);
		}

		PlayHand PH = new PlayHand();

		Integer handID1 = PH.addHand(...)

		Integer handID2 = PH.addHand(...)

		PH.listHands();

		ME.updateHand(handID1,...)
	}

	public Integer addHand(UUID playerID, int HandStrength, int HiHand, int LoHand, int Kicker){
      Session session = factory.openSession();
      Transaction tx = null;
      Integer handID = null;
      try{
         tx = session.beginTransaction();
         Hand hand = new Hand;
    	 ScoreHand(eHandStrength hST, int HiHand, int LoHand, int Kicker);
         handID = (Integer) session.save(hand); 
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
         session.close(); 
      }
      return handID;
   }

   /* Method to list all the employees detail */
   public void listEmployees( ){
      Session session = factory.openSession();
      Transaction tx = null;
      try{
         tx = session.beginTransaction();
         List employees = session.createQuery("FROM Employee").list(); 
         for (Iterator iterator1 = 
                           employees.iterator(); iterator1.hasNext();){
            Employee employee = (Employee) iterator1.next(); 
            System.out.print("First Name: " + employee.getFirstName()); 
            System.out.print("  Last Name: " + employee.getLastName()); 
            System.out.println("  Salary: " + employee.getSalary());

         }
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
         session.close(); 
      }
   }
   /* Method to update salary for an employee */
   public void updateEmployee(Integer EmployeeID, int salary ){
      Session session = factory.openSession();
      Transaction tx = null;
      try{
         tx = session.beginTransaction();
         Employee employee = 
                    (Employee)session.get(Employee.class, EmployeeID); 
         employee.setSalary( salary );
         session.update(employee);
         tx.commit();
      }catch (HibernateException e) {
         if (tx!=null) tx.rollback();
         e.printStackTrace(); 
      }finally {
         session.close(); 
      }
   }

   //End Hibernate

	public static void main(String[] args) throws Exception {

		PlayJokerWild();
	}


	private static void PlayJokerWild() throws Exception {

		// Table is created
		Table tbl = new Table();

		// Rule set is called (A given game has a rule set)
		Rule rle = new Rule(eGame.DeucesWild);

		// Game is created (tables have players, players play games, etc)
		GamePlay gme = new GamePlay(rle);
		tbl.AddGame(gme);

		// Deck is created using game's ruleset
		Deck d = new Deck(gme.GetNumberOfJokers(), rle.GetRuleCards());

		Map PlayerMap = new HashMap();
		
		// Add the players to Table and Game, give them empty hands
		for (int i = 0; i < gme.GetMaxNumberOfPlayers(); i++) {
			Player p = new Player(null);
			switch (i) {
			case 0:
				p.SetPlayerName("Bob");
				break;
			case 1:
				p.SetPlayerName("Jim");
				break;
			case 2:
				p.SetPlayerName("Joe");
				break;
			case 3:
				p.SetPlayerName("Jane");
				break;				
			}
			PlayerMap.put(p.GetPlayerID(), p);			
			p.SetPlayerNbr(i + 1);
			tbl.AddTablePlayer(p);
			gme.AddGamePlayer(p);
			p.SetHand(new Hand());
		}

		// Five Card in each hand
		for (int i = 0; i < gme.GetNumberOfCards(); i++) {
			for (Player p : gme.GetGamePlayers()) {
				Hand h = p.GetHand();
				h.setPlayerID(p.GetPlayerID());
				h.AddCardToHand(d.drawFromDeck());
				p.SetHand(h);
			}
		}

		// Handle jokers
		for (Player p : gme.GetGamePlayers()) {
			Hand h = p.GetHand();
			h.HandleJokerWilds();
			p.SetHand(h);
		}

		// Collect All Hands in a Game
		ArrayList<Hand> AllHands = new ArrayList<Hand>();
		for (Player p : gme.GetGamePlayers()) {
			AllHands.add(p.GetHand());
		}

		// Find the best hand between players
		Collections.sort(AllHands, Hand.HandRank);
		
		//	For Lab #5...  for each "h", write it to the database.
		// 	I want ONLY the following elements saved for each record:
		//  playerID;
		//	HandStrength;
		//  Natural
		//	HiHand;
		//	LoHand;
		//	Kicker;
		for (Player p : gme.GetGamePlayers()) {
			Hand h = p.GetHand();
		}
		
		Player WinningPlayer = (Player) PlayerMap.get(AllHands.get(0).getPlayerID());
		System.out.println(WinningPlayer.GetPlayerName().toString() + " is the winner!!");	
		System.out.println(AllHands.get(0).getHandStrength());

		for (Card c : AllHands.get(0).getCards()) {
			System.out.print(c.getRank());
			System.out.print(" ");
			System.out.print(c.getSuit());
			System.out.print("      ");
		}

		System.out.println("");
		System.out.println("");
		System.out.println("");

		/*
		 * StringWriter swTable = tbl.SerializeMe();
		 * System.out.println("*******   Table XML   *******");
		 * System.out.println(swTable.toString());
		 * 
		 * StringWriter swDeck = d.SerializeMe();
		 * System.out.println("*******   Deck XML   *******");
		 * System.out.println(swDeck.toString());
		 */
	}
}