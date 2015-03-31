# self-efficacy-condition-assigner
Parses survey responses, calculates self-efficacy median split, assigns participants to heterogeneous/homogenous pairs, and then randomly assigns to condition.

For a field experiment in a classroom I needed to assign participants to one of three conditions, and assign them to one of three types of pairs (homogenousx2, heterogeneous) based on their self-reported self-efficacy. Students arrived at class, completed a questionnaire, and while they took their pretest, my condition assigner computed each student's self-efficacy, determined the median (to create "high" and "low" self-efficacy categorizations), created pairs, and evenly assigned types of pairs to a condition (targeting high/low/neither self-efficacy). I wrote the condition assigner in Java.

The very basic process is as follows: 
(1) SEAnalyzer calls on ParticipantCreator to read the survey file and create a list of Participant objects with necessary information in the data structure. 
(2) ParticipantCreator is also in charge of calculating self-efficacy scores, and the self-efficacy median split values of each participant. 
(3) The participant array is then fed to the TeamAssigner, which assigns team composition types based upon what the user asked (although not entirely possible all the time, since you may run out of one type of a student or another). 
(4) The teams are then passed to the ConditionAssigner, which attempts to evenly distribute each of the team composition types across the three conditions (target high, target low, neutral). 
(5) ConditionAssigner also communicates with the TutorWriter, which is in charge of formatting information in such a way that your tutor software can interpret it. 
(6) TutorWriter does some other things with outputting files and keeping track of what happened in the past (which would be useful if I had the time to implement allowing the user to request certain numbers of teams in conditions). 

'SurveyResponses.csv' is a sample [simulated] dataset that is in the proper format, with randomized responses. 
'instructions.txt' provides a more in-depth list of instructions for experimenters on the day-of.

An overview of the data collection and analysis is included here: www.irishowley.com/website/pAssigner.html
