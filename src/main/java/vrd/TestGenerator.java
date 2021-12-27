package vrd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vrd.base.*;

import java.util.*;

@Component
public class TestGenerator {

    @Autowired
    GroupRepository groupRepository;
    @Autowired
    CurrentTestRepository currentTestRepository;
    @Autowired
    CurrentQuestionRepository currentQuestionRepository;

    private static class Counter {
        private int counter;
        Counter() {
            counter = 0;
        }
        public void inc() {
            ++counter;
        }
        public int getCounter() {
            return counter;
        }
    }

    public Long generateCurrentTest(User user, Test test, Session session) {
        {
            Optional<CurrentTest> ext = currentTestRepository.findBySessionAndUser(session, user);
            if (ext.isPresent())
                return ext.get().getId();
        }
        long time = new Date().getTime();
        for (int i = 0; i < user.getUsername().length(); ++i)
            time = (time*((long)user.getUsername().charAt(i)) + (long)user.getUsername().charAt(i)) & 0xFFFFFFFFFFL;
        Random random = new Random(time);

        List<Group> groups = groupRepository.findFromTest(test.getId());
        CurrentTest currentTest = new CurrentTest(test, user);
        currentTest.setDateStart(new Date());
        currentTest.setSession(session);

        currentTest = currentTestRepository.save(currentTest);
        Counter questionCount = new Counter();

        ArrayList<Group> selectedGroups = new ArrayList<Group>();
        int index = 0;
        for (Group group: groups) {
            if (selectedGroups.isEmpty()) {
                selectedGroups.add(group);
                continue;
            }
            if (index < test.getRelations().length()) {
                char relation = test.getRelations().charAt(index);
                if (relation == ',') {
                    appendGroups(currentTest, selectedGroups, random, questionCount);
                    selectedGroups.clear();
                }
            }
            selectedGroups.add(group);
            ++index;
        }
        if (selectedGroups.size() > 0)
            appendGroups(currentTest, selectedGroups, random, questionCount);

        return currentTest.getId();
    }

    private void appendGroups(CurrentTest currentTest, ArrayList<Group> selectedGroups, Random random, Counter questionCount) {
        if (selectedGroups.isEmpty()) {
            return;
        }

        Group group = selectedGroups.get(random.nextInt(selectedGroups.size()));
        LinkedList<Quest> quests = new LinkedList<>(group.getQuests());

        for (int i = 0; i < group.getQuestCount(); ++i) {
            int select = random.nextInt(quests.size());

            Quest quest = quests.get(select);
            quests.remove(select);

            CurrentQuestion currentQuest = new CurrentQuestion(currentTest, questionCount.getCounter(), quest);
            currentQuestionRepository.save(currentQuest);
            questionCount.inc();

        }
    }
}
