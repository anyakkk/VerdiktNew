package vrd.compiler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import vrd.base.*;
import vrd.repository.*;

import java.util.*;

@Component

public class Analizator {

    @Autowired
    TestRepository testRepository;
    @Autowired
    CurrentTestRepository currentTestRepository;
    @Autowired
    AnswerRepository answerRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    QuestRepository questRepository;

    private static class GroupInfo {
        public int fromIdx;
        public int toIdx;
        public int count;

        public GroupInfo(Reader reader) {
            fromIdx = readNumber(reader);
            reader.nextCh();
            toIdx = readNumber(reader);
            if (reader.nowCh() != '-') {
                count = 0;
            } else {
                reader.nextCh();
                count = readNumber(reader);
            }
        }
    }

    public void parse(Reader reader, Test test) {
        boolean firstLine = true;
        boolean readHeader = false;
        StringBuilder header = new StringBuilder();

        TreeMap<Integer, ArrayList<Quest>> quests = new TreeMap<>();

        ArrayList<GroupInfo> groups = new ArrayList<GroupInfo>();
        StringBuilder relations = new StringBuilder();

        while (reader.isNotOverflow()) {
            if (reader.nowCh() == '&') {
                this.readComment(reader);
            } else if (reader.nowCh() == '?') {
                firstLine = false;
                this.readQuestion(reader, quests);
            } else if (firstLine) {
                if (readHeader) {
                    header.append(reader.nowCh());
                    reader.nextCh();
                } else if (isHeader(reader)) {
                    readHeader = true;
                    readComment(reader);
                } else if (Character.isDigit(reader.nowCh())) {
                    this.readCommandLine(reader, groups, relations);
                } else {
                    reader.nextCh();
                }
            } else
                reader.nextCh();
        }

        test.setRelations(relations.toString());
        test.setHeader(header.toString());
        testRepository.save(test);

        for (int i = 0; i < groups.size(); ++i) {
            GroupInfo info = groups.get(i);
            NavigableMap<Integer, ArrayList<Quest>> currentQuests = quests.subMap(info.fromIdx, true, info.toIdx, true);

            Group group = new Group();
            group.setTest(test);
            group.setOrder(i);
            group.setQuestCount(0);
            group = groupRepository.save(group);

            int questCount = 0;
            for (Map.Entry<Integer, ArrayList<Quest>> entry : currentQuests.entrySet()) {
                for (Quest quest : entry.getValue()) {
                    ++questCount;
                    Quest toSave = new Quest();
                    toSave.setText(quest.getText());
                    toSave.setVariant(quest.getVariant());
                    toSave.setTest(test);
                    toSave = questRepository.save(toSave);

                    group.getQuests().add(toSave);
                    toSave.getGroups().add(group);

                    for (Answer answer : quest.getAnswers()) {
                        answer.setQuest(toSave);
                        answerRepository.save(answer);
                    }
                }
            }

            group.setQuestCount(Math.min(questCount, info.count == 0? questCount : info.count));
            groupRepository.save(group);
        }

    }


    public void readComment(Reader reader) {
        while ((reader.isNotOverflow()) && (reader.nowCh() == '\n')) {
            reader.nextCh();
        }
        if (reader.isNotOverflow())
            reader.nextCh();
    }

    public boolean isHeader(Reader reader) {
        int current = 0;
        final String KEYWORD = "заголовок";
        while ((current < KEYWORD.length()) &&  (reader.isNotOverflow()) && ( Character.toLowerCase(reader.nowCh()) == KEYWORD.charAt(current))) {
            ++current;
            reader.nextCh();
        }
        return (current == KEYWORD.length());
    }

    public static int readNumber(Reader reader) {
        int result = 0;
        while (Character.isDigit(reader.nowCh())) {
            result = result*10 + (reader.nowCh() - '0');
            reader.nextCh();
        }
        return result;
    }

    public void readQuestion(Reader reader, TreeMap<Integer, ArrayList<Quest>> quests) {
        reader.nextCh();
        int number = readNumber(reader);
        Quest quest = new Quest();
        boolean readingBody = true;
        boolean readingAnswer = false;
        int count = 0;
        readComment(reader);
        if (!quests.containsKey(number)) {
            quests.put(number, new ArrayList<>());
        }
        quests.get(number).add(quest);

        StringBuilder body = new StringBuilder();

        while (reader.isNotOverflow()) {
            if (reader.getPosInLine() == 0) {
                if (reader.nowCh() == '?') {
                    break;
                }

                if (reader.nowCh() == '&') {
                    readComment(reader);
                } else if (reader.nowCh() == '!') {
                    readingBody = false;
                    reader.nextCh();
                    char c = Character.toLowerCase(reader.nowCh());
                    if ((c == 'м') || (c == 'm')) {
                        quest.setVariant(true);
                    } else if ((c == 'ч') || (c == 'с') || (c == 'c')) {
                        quest.setVariant(false);
                        readingAnswer = true;
                    }
                    readComment(reader);
                } else if (reader.nowCh() == '+') {
                    readingBody = false;
                    reader.nextCh();
                    Answer answer = new Answer(count++, quest, reader.readLineNoEnd(), true);
                    quest.getAnswers().add(answer);
                } else if (reader.nowCh() == '-') {
                    readingBody = false;
                    reader.nextCh();
                    Answer answer = new Answer(count++, quest, reader.readLineNoEnd(), false);
                    quest.getAnswers().add(answer);
                } else {
                    if (readingBody) {
                        body.append(reader.nowCh());
                        reader.nextCh();
                    } else if (readingAnswer) {
                        String line = reader.readLineNoEnd();
                        if (line.length() > 0) {
                            Answer answer = new Answer(count++, quest, line, true);
                            quest.getAnswers().add(answer);
                        }
                    } else {
                        reader.nextCh();
                    }
                }
            } else {
                if (readingBody) {
                    body.append(reader.nowCh());
                }
                reader.nextCh();
            }
        }
        quest.setText(body.toString());

    }

    public void readCommandLine(Reader reader,  ArrayList<GroupInfo> groups, StringBuilder relations) {
        groups.add(new GroupInfo(reader));
        char mem = '-';
        while ((reader.nowCh() == ';')
                || (reader.nowCh() == ',')
                || (reader.nowCh() == '\n')
                || (reader.nowCh() == '&')
                || (reader.nowCh() == ' ')
                || (Character.isDigit(reader.nowCh()))) {
            if (reader.nowCh() == ';' || reader.nowCh() == ',') {
                mem = reader.nowCh();
                reader.nextCh();
                continue;
            }
            if ((reader.nowCh() == '\n') || (reader.nowCh() == ' ')) {
                reader.nextCh();
                continue;
            }
            if (reader.nowCh() == '&') {
                readComment(reader);
                continue;
            }
            if (Character.isDigit(reader.nowCh())) {
                if (mem == '-')
                    break;

                relations.append(mem);
                groups.add(new GroupInfo(reader));
            }
        }
    }
}
