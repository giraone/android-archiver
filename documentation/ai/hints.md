# Hints on AI / Claude

Question: *What is the best way to formulate new requirements for the app without repeating non-functional requirements and technical instructions?*

● Focus on user value and behavior rather than technical implementation. Here's the most effective approach:

Structure Requirements As User Stories

As a [user type], I want to [action/capability] so that [benefit/outcome]

Focus On These Elements

- What the user needs to accomplish
- Why it provides value
- When/Where it applies
- Business rules and constraints
- Success criteria (measurable outcomes)

Avoid Repeating

- Architecture patterns (MVVM, Repository, etc.)
- Technology choices (Compose, DataStore, etc.)
- Performance specs
- Platform constraints
- Code quality standards

Example

Instead of: "Implement a settings screen using Compose with DataStore persistence following MVVM pattern"

Write: "As a user, I want to customize app behavior (file naming, sort preferences, auto-delete) so I can optimize my workflow for different use cases"

Pro Tips

- Use acceptance criteria for specific behaviors
- Group related features into epics
- Reference existing technical docs (like your CLAUDE.md) for non-functional aspects
- Focus on user outcomes over technical tasks

This keeps requirements clean, focused on value, and avoids technical repetition while your CLAUDE.md handles the "how."

---