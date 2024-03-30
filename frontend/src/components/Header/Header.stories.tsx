import type { Meta, StoryObj } from '@storybook/react';

import Header from './Header';

const meta = {
  title: 'Common/Header',
  component: Header,
  parameters: {},
  tags: ['autodocs'],
  argTypes: {},
} satisfies Meta<typeof Header>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Divided: Story = {
  args: {
    children: <h1 className="custom-bold-text">날씨 주식</h1>,
  },
};
